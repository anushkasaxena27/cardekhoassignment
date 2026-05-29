package com.cardekho.security;

import com.cardekho.entity.AppUser;
import com.cardekho.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DomainUserDetailsService implements UserDetailsService {

	private final AppUserRepository users;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		AppUser u = users.findByEmailIgnoreCase(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not found"));
		var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + u.getRole()));
		return User.builder()
				.username(u.getEmail())
				.password(u.getPasswordHash())
				.authorities(authorities)
				.build();
	}
}
