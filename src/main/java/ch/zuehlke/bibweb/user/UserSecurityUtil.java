package ch.zuehlke.bibweb.user;

import org.springframework.security.core.context.SecurityContextHolder;

public class UserSecurityUtil {
    public static boolean currentUserHasAuthority(final String neededAuthority) {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(neededAuthority));
    }

    public static String currentUserName() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public static BibwebUser getCurrentUser() {
        return (BibwebUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    private UserSecurityUtil() {
        throw new IllegalStateException("Utility class");
    }
}
