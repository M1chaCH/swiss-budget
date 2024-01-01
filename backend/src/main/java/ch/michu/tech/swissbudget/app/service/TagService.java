package ch.michu.tech.swissbudget.app.service;


import ch.michu.tech.swissbudget.app.dto.tag.TagDto;
import ch.michu.tech.swissbudget.app.dto.tag.UpdateTagDto;
import ch.michu.tech.swissbudget.app.entity.CompleteTransactionEntity;
import ch.michu.tech.swissbudget.app.exception.KeywordAlreadyExistsException;
import ch.michu.tech.swissbudget.app.exception.TagAlreadyExistsException;
import ch.michu.tech.swissbudget.app.provider.KeywordProvider;
import ch.michu.tech.swissbudget.app.provider.TagProvider;
import ch.michu.tech.swissbudget.app.provider.TransactionProvider;
import ch.michu.tech.swissbudget.app.transaction.TransactionTagMapper;
import ch.michu.tech.swissbudget.framework.data.RequestSupport;
import ch.michu.tech.swissbudget.framework.error.exception.ResourceNotFoundException;
import ch.michu.tech.swissbudget.generated.jooq.tables.records.KeywordRecord;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import java.util.List;
import java.util.UUID;
import org.jooq.exception.TooManyRowsException;

@ApplicationScoped
public class TagService {

    private final Provider<RequestSupport> supportProvider;
    private final TransactionProvider transactionProvider;
    private final TagProvider tagProvider;
    private final KeywordProvider keywordProvider;
    private final TransactionTagMapper mapper;

    @Inject
    public TagService(Provider<RequestSupport> supportProvider, TransactionProvider transactionProvider, TagProvider tagProvider,
        KeywordProvider keywordProvider, TransactionTagMapper mapper) {
        this.supportProvider = supportProvider;
        this.transactionProvider = transactionProvider;
        this.tagProvider = tagProvider;
        this.keywordProvider = keywordProvider;
        this.mapper = mapper;
    }

    public List<TagDto> getTags() {
        return tagProvider.selectTagsWithKeywordsByUserIdAsDto(supportProvider.get().getUserIdOrThrow());
    }

    public void createTag(UpdateTagDto toCreate) {
        toCreate.getKeywordsToAdd().forEach(this::validateNewKeyword);
        UUID userId = supportProvider.get().getUserIdOrThrow();
        UUID tagId = toCreate.getTagId() == null ? UUID.randomUUID() : toCreate.getTagId();

        if (tagProvider.fetchExists(userId, tagId, toCreate.getName())) {
            throw new TagAlreadyExistsException(toCreate.getName());
        }

        tagProvider.insertCompleteTag(userId, tagId, toCreate.getName(), toCreate.getColor(), toCreate.getIcon(),
            toCreate.getKeywordsToAdd());

        List<KeywordRecord> addedKeywords = keywordProvider.selectKeywordsByTagId(userId, tagId);
        mapper.handleKeywordsAdded(userId, tagId, addedKeywords);
    }

    // not allowed to update keywords, would be waaaay too much pain to make sure all transactions are correct -> user has to delete and add again
    public void updateTag(UpdateTagDto toUpdate) {
        UUID userId = supportProvider.get().getUserIdOrThrow();

        if (!tagProvider.fetchExists(userId, toUpdate.getTagId())) {
            throw new ResourceNotFoundException("tag", toUpdate.getTagId());
        }
        if (tagProvider.fetchExists(userId, toUpdate.getTagId(), toUpdate.getName())) {
            throw new TagAlreadyExistsException(toUpdate.getName());
        }
        toUpdate.getKeywordsToAdd().forEach(this::validateNewKeyword);

        tagProvider.updateTag(userId, toUpdate.getTagId(), toUpdate.getName(), toUpdate.getColor(), toUpdate.getIcon());
        keywordProvider.deleteKeywordsByIds(userId, toUpdate.getKeywordIdsToDelete());
        keywordProvider.insertKeywordsToTag(userId, toUpdate.getTagId(), toUpdate.getKeywordsToAdd());

        List<KeywordRecord> addedKeywords = keywordProvider.selectKeywordsByTagId(userId, toUpdate.getTagId());
        mapper.handleKeywordsAdded(userId, toUpdate.getTagId(), addedKeywords);
    }

    public void deleteTag(UUID tagId) {
        UUID userId = supportProvider.get().getUserIdOrThrow();

        UUID defaultTagId = tagProvider.selectDefaultTagId(userId);
        transactionProvider.updateTransactionsByTagWithDefaultTag(tagId, defaultTagId);

        tagProvider.deleteById(userId, tagId);
    }

    public void validateNewKeyword(String keyword) {
        try {
            KeywordProvider.KeywordWithTagEntity existingKeyword = this.keywordProvider.selectByKeywordWithTagName(
                supportProvider.get().getUserIdOrThrow(), keyword);
            if (existingKeyword != null) {
                throw new KeywordAlreadyExistsException(existingKeyword.tagName(), existingKeyword.keyword());
            }
        } catch (TooManyRowsException e) {
            throw new KeywordAlreadyExistsException("(multiple...)", keyword);
        }
    }

    public void resolveConflict(UUID transactionId, UUID selectedTagId, UUID matchingKeywordId, boolean removeOthers) {
        RequestSupport support = supportProvider.get();
        UUID userId = support.getUserIdOrThrow();

        if (!transactionProvider.fetchExists(userId, transactionId)) {
            throw new ResourceNotFoundException("transaction", transactionId);
        }
        if (!tagProvider.fetchExists(userId, selectedTagId)) {
            throw new ResourceNotFoundException("tag", selectedTagId);
        }
        if (!keywordProvider.fetchExists(userId, matchingKeywordId)) {
            throw new ResourceNotFoundException("keyword", matchingKeywordId);
        }

        CompleteTransactionEntity transaction = transactionProvider.selectCompleteTransaction(userId, transactionId);

        if (removeOthers) {
            // delete keywords from duplicates that are not the newly selected
            transaction.getTagDuplicates().forEach(duplicate -> {
                if (!duplicate.getMatchingKeyword().getId().equals(matchingKeywordId)) {
                    duplicate.getMatchingKeyword().delete();
                }
            });

            // if old matching keyword is not in selected tag -> delete this keyword
            if (!transaction.getTransaction().getMatchingKeywordId().equals(matchingKeywordId)) {
                transaction.getMatchingKeyword().delete();
            }
        }

        transactionProvider.updateTransactionWithTagAndRemoveNeedAttention(transactionId, selectedTagId, matchingKeywordId);
        transactionProvider.deleteAllTagDuplicates(transactionId);
    }

    public void assignTag(UUID transactionId, UUID tagId, String keyword) {
        RequestSupport support = supportProvider.get();
        UUID userId = support.getUserIdOrThrow();

        if (keyword != null) {
            validateNewKeyword(keyword);
        }
        if (!transactionProvider.fetchExists(userId, transactionId)) {
            throw new ResourceNotFoundException("transaction", transactionId);
        }
        if (!tagProvider.fetchExists(userId, tagId)) {
            throw new ResourceNotFoundException("tag", "" + tagId);
        }

        if (keyword != null) {
            this.addKeyword(support, tagId, keyword);
        } else {
            transactionProvider.updateTransactionWithTagAndRemoveNeedAttention(transactionId, tagId);
        }
    }

    private void addKeyword(RequestSupport support, UUID tagId, String keyword) {
        support.logInfo(this, "adding keyword to tag: %s->%s", tagId, keyword);
        UUID userId = support.getUserIdOrThrow();
        UUID newKeywordId = UUID.randomUUID();
        keywordProvider.insertKeywordToTag(userId, newKeywordId, tagId, keyword);
        KeywordRecord newKeyword = keywordProvider.newRecord();
        newKeyword.setId(newKeywordId);
        newKeyword.setUserId(userId);
        newKeyword.setTagId(tagId);
        newKeyword.setKeyword(keyword);

        mapper.handleKeywordsAdded(userId, tagId, List.of(newKeyword));
    }
}
