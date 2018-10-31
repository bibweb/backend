package ch.zuehlke.bibweb.user;

import org.springframework.security.core.context.SecurityContextHolder;

public class UserSecurityUtil {
    public static boolean currentUserHasRole(final String neededRole) {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(neededRole));
    }

    public static String currentUserName() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public static BibwebUser getCurrentUser() {
        return (BibwebUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
