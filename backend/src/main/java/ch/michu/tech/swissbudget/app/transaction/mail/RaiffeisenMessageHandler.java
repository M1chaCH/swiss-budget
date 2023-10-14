package ch.michu.tech.swissbudget.app.transaction.mail;

import ch.michu.tech.swissbudget.app.transaction.SupportedBank;
import ch.michu.tech.swissbudget.generated.jooq.tables.records.TransactionMailRecord;
import ch.michu.tech.swissbudget.generated.jooq.tables.records.TransactionRecord;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class RaiffeisenMessageHandler extends MailContentHandler {

    public static final String EXPENSE_SUBJECT = "Raiffeisen E-Banking - Belastung wurde ausgelöst";
    public static final String INCOME_SUBJECT = "Raiffeisen E-Banking - Gutschrift ist eingetroffen";
    public static final String FROM_ADDRESS = "noreply@ebanking.raiffeisen.ch";
    public static final String TRANSACTION_DATE_ID = "\nBuchungsdatum: ";
    public static final String BANKACCOUNT_ID = "\nKonto: ";
    public static final String AMOUNT_ID = "\nBetrag: ";
    public static final String RECEIVER_ID = "\nBuchung:\n";
    public static final String RECEIVER_END_ID = "\nFreundliche Grüsse\n";

    protected final DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    @Override
    public boolean validateFromBank(TransactionMailRecord mail) {
        return (mail.getSubject().equals(EXPENSE_SUBJECT) || mail.getSubject().equals(INCOME_SUBJECT)) && mail.getFromMail()
            .equals(FROM_ADDRESS);
    }

    @Override
    public void parseTransaction(TransactionRecord transaction, TransactionMailRecord mail) {
        String content = mail.getRawMessage();

        transaction.setId(UUID.randomUUID().toString());
        transaction.setExpense(mail.getSubject().equals(EXPENSE_SUBJECT));

        int transactionDateIndex = content.indexOf(TRANSACTION_DATE_ID) + TRANSACTION_DATE_ID.length();
        String transactionDate = content.substring(transactionDateIndex, transactionDateIndex + 10);
        transaction.setTransactionDate(LocalDate.parse(transactionDate, dayFormatter));

        int bankAccountStartIndex = content.indexOf(BANKACCOUNT_ID) + BANKACCOUNT_ID.length();
        int bankAccountEndIndex = content.indexOf("\n", bankAccountStartIndex);
        transaction.setBankaccount(content.substring(bankAccountStartIndex, bankAccountEndIndex));

        int amountStartIndex = content.indexOf(AMOUNT_ID) + AMOUNT_ID.length();
        int amountEndIndex = content.indexOf(" CHF\n", amountStartIndex);
        String amountString = content.substring(amountStartIndex, amountEndIndex).replace("'", "");
        transaction.setAmount(Double.parseDouble(amountString));

        int receiverStartIndex = content.indexOf(RECEIVER_ID) + RECEIVER_ID.length();
        int receiverEndIndex = content.indexOf(RECEIVER_END_ID, amountStartIndex);
        transaction.setReceiver(content.substring(receiverStartIndex, receiverEndIndex));
    }

    @Override
    public SupportedBank getSupportedBank() {
        return SupportedBank.RAIFFEISEN;
    }
}
