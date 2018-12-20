package ch.skunky.authserver.service;

import ch.skunky.authserver.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.security.auth.Subject;
import java.nio.file.attribute.UserPrincipal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class SkunkUserPrincipal implements UserDetails {

    @Getter
    private User user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String roleString = user.getRoles();
        if(roleString==null || roleString.isBlank() || roleString.isEmpty()) return Collections.emptyList();

        String[] roles = roleString.split(",");
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>(roles.length);
        for(String role : roles){
            grantedAuthorities.add(new SimpleGrantedAuthority(role.trim()));
        }
        return grantedAuthorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !user.isLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
