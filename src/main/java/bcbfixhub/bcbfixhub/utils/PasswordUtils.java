package bcbfixhub.bcbfixhub.utils;

import org.mindrot.jbcrypt.BCrypt;

/**
 * The Password utils. Class containing utilities that uses the jbcrypt library.
 * @method
 */
public class PasswordUtils {
    /**
     * Hash password string.
     *
     * @param plainPassword the plain password
     * @return the string
     */
    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));
    }

    /**
     * Verify password boolean.
     *
     * @param plainPassword  the plain password
     * @param hashedPassword the hashed password
     * @return the boolean
     */
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}
