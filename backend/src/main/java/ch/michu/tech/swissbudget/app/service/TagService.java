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
        final RequestSupport support = supportProvider.get();
        return tagProvider.selectTagsWithKeywordsByUserIdAsDto(support.db(), support.getUserIdOrThrow());
    }

    public void createTag(UpdateTagDto toCreate) {
        final RequestSupport support = supportProvider.get();
        toCreate.getKeywordsToAdd().forEach(this::validateNewKeyword);
        UUID userId = support.getUserIdOrThrow();
        UUID tagId = toCreate.getTagId() == null ? UUID.randomUUID() : toCreate.getTagId();

        if (tagProvider.fetchExists(support.db(), userId, tagId, toCreate.getName())) {
            throw new TagAlreadyExistsException(toCreate.getName());
        }

        tagProvider.insertCompleteTag(support.db(), userId, tagId, toCreate.getName(), toCreate.getColor(), toCreate.getIcon(),
            toCreate.getKeywordsToAdd());

        List<KeywordRecord> addedKeywords = keywordProvider.selectKeywordsByTagId(support.db(), userId, tagId);
        mapper.handleKeywordsAdded(support.db(), userId, tagId, addedKeywords);
    }

    // not allowed to update keywords, would be waaaay too much pain to make sure all transactions are correct -> user has to delete and add again
    public void updateTag(UpdateTagDto toUpdate) {
        final RequestSupport support = supportProvider.get();
        UUID userId = support.getUserIdOrThrow();

        if (!tagProvider.fetchExists(support.db(), userId, toUpdate.getTagId())) {
            throw new ResourceNotFoundException("tag", toUpdate.getTagId());
        }
        if (tagProvider.fetchExists(support.db(), userId, toUpdate.getTagId(), toUpdate.getName())) {
            throw new TagAlreadyExistsException(toUpdate.getName());
        }
        toUpdate.getKeywordsToAdd().forEach(this::validateNewKeyword);

        tagProvider.updateTag(support.db(), userId, toUpdate.getTagId(), toUpdate.getName(), toUpdate.getColor(), toUpdate.getIcon());
        keywordProvider.deleteKeywordsByIds(support.db(), userId, toUpdate.getKeywordIdsToDelete());
        keywordProvider.insertKeywordsToTag(support.db(), userId, toUpdate.getTagId(), toUpdate.getKeywordsToAdd());

        List<KeywordRecord> addedKeywords = keywordProvider.selectKeywordsByTagId(support.db(), userId, toUpdate.getTagId());
        mapper.handleKeywordsAdded(support.db(), userId, toUpdate.getTagId(), addedKeywords);
    }

    public void deleteTag(UUID tagId) {
        final RequestSupport support = supportProvider.get();
        UUID userId = support.getUserIdOrThrow();

        UUID defaultTagId = tagProvider.selectDefaultTagId(support.db(), userId);
        transactionProvider.updateTransactionsByTagWithDefaultTag(support.db(), tagId, defaultTagId);

        tagProvider.deleteById(support.db(), userId, tagId);
    }

    public void validateNewKeyword(String keyword) {
        try {
            final RequestSupport support = supportProvider.get();
            KeywordProvider.KeywordWithTagEntity existingKeyword = this.keywordProvider.selectByKeywordWithTagName(
                support.db(), support.getUserIdOrThrow(), keyword);
            if (existingKeyword != null) {
                throw new KeywordAlreadyExistsException(existingKeyword.tagName(), existingKeyword.keyword());
            }
        } catch (TooManyRowsException e) {
            throw new KeywordAlreadyExistsException("(multiple...)", keyword);
        }
    }

    public void resolveConflict(UUID transactionId, UUID selectedTagId, UUID matchingKeywordId, boolean removeOthers) {
        final RequestSupport support = supportProvider.get();
        UUID userId = support.getUserIdOrThrow();

        if (!transactionProvider.fetchExists(support.db(), userId, transactionId)) {
            throw new ResourceNotFoundException("transaction", transactionId);
        }
        if (!tagProvider.fetchExists(support.db(), userId, selectedTagId)) {
            throw new ResourceNotFoundException("tag", selectedTagId);
        }
        if (!keywordProvider.fetchExists(support.db(), userId, matchingKeywordId)) {
            throw new ResourceNotFoundException("keyword", matchingKeywordId);
        }

        CompleteTransactionEntity transaction = transactionProvider.selectCompleteTransaction(support.db(), userId, transactionId);

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

        transactionProvider.updateTransactionWithTagAndRemoveNeedAttention(support.db(), transactionId, selectedTagId, matchingKeywordId);
        transactionProvider.deleteAllTagDuplicates(support.db(), transactionId);
    }

    public void assignTag(UUID transactionId, UUID tagId, String keyword) {
        final RequestSupport support = supportProvider.get();
        UUID userId = support.getUserIdOrThrow();

        if (keyword != null) {
            validateNewKeyword(keyword);
        }
        if (!transactionProvider.fetchExists(support.db(), userId, transactionId)) {
            throw new ResourceNotFoundException("transaction", transactionId);
        }
        if (!tagProvider.fetchExists(support.db(), userId, tagId)) {
            throw new ResourceNotFoundException("tag", "" + tagId);
        }

        if (keyword != null) {
            this.addKeyword(support, tagId, keyword);
        } else {
            transactionProvider.updateTransactionWithTagAndRemoveNeedAttention(support.db(), transactionId, tagId);
        }
    }

    private void addKeyword(RequestSupport support, UUID tagId, String keyword) {
        support.logInfo(this, "adding keyword to tag: %s->%s", tagId, keyword);
        UUID userId = support.getUserIdOrThrow();
        UUID newKeywordId = UUID.randomUUID();
        keywordProvider.insertKeywordToTag(support.db(), userId, newKeywordId, tagId, keyword);
        KeywordRecord newKeyword = keywordProvider.newRecord(support.db());
        newKeyword.setId(newKeywordId);
        newKeyword.setUserId(userId);
        newKeyword.setTagId(tagId);
        newKeyword.setKeyword(keyword);

        mapper.handleKeywordsAdded(support.db(), userId, tagId, List.of(newKeyword));
    }
}
