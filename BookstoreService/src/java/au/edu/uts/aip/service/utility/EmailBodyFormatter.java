package au.edu.uts.aip.service.utility;

public class EmailBodyFormatter {
    private static final String SALUTATION_PREFIX = "Dear";
    private static final String SIGNATURE_PREFIX = "Regards";
    private static final String SENDER_NAME = "The Bookstore Team";
    private static final String BODY_ACCOUNT_ACTIVATION = 
            "Welcome to Bookstore!\n\nTo get started, please activate your account by clicking the "
            + "link below:\n%1$s";
    private static final String BODY_VERIFICATION_REJECT = 
            "Thank you for submitting your documents for identity verification. "
            + "However, we are unable to approve your submission due to the following reason:\n\n"
            + "\t%1$s";
    private static final String BODY_VERIFICATION_APPROVE = 
            "Thank you for submitting your documents for identity verification. "
            + "Your account is now verified.";
    private static final String BODY_ACCOUNT_BAN = 
            "Your account has been banned.";
    private static final String BODY_ACCOUNT_UNBAN = 
            "Your account has been unbanned.";
    
    public static String onAccountActivation(String name, String activationUrl) {
        return format(name, String.format(BODY_ACCOUNT_ACTIVATION, activationUrl));
    }
    
    public static String onVerificationReject(String name, String reason) {
        return format(name, String.format(BODY_VERIFICATION_REJECT, reason));
    }
    
    public static String onVerificationApprove(String name) {
        return format(name, BODY_VERIFICATION_APPROVE);
    }
    
    public static String onAccountBan(String name) {
        return format(name, BODY_ACCOUNT_BAN);
    }
    
    public static String onAccountUnban(String name) {
        return format(name, BODY_ACCOUNT_UNBAN);
    }
    
    public static String format(String name, String info) {
        return String.format(
                "%1$s %2$s,\n\n%3$s\n\n%4$s,\n%5$s", 
                SALUTATION_PREFIX, name, info, SIGNATURE_PREFIX, SENDER_NAME
        );
    }
}
