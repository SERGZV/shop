package ru.shop.three_d_print.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.ObjectError;

import ru.shop.three_d_print.entities.Role;
import ru.shop.three_d_print.entities.RoleType;
import ru.shop.three_d_print.entities.User;
import ru.shop.three_d_print.repository.RoleRepository;
import ru.shop.three_d_print.repository.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService
{
    @PersistenceContext
    private EntityManager em;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public User newUser() { return new User(); }

    public List<ObjectError> trySaveUser(User user)
    {
        List<ObjectError> checkResult = ManualUserValidation(user);
        if(checkResult.size() > 0) { return checkResult; }

        user.setRoles(Collections.singleton(new Role(2L, RoleType.USER.get())));
        user.setUnencryptedPassword(user.getPassword());
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        return checkResult;
    }

    public String getCurrentUsername()
    {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }

    public User loadUserById(Long userId)
    {
        Optional<User> userFromDb = userRepository.findById(userId);
        return userFromDb.orElse(new User());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
    {
        User user = userRepository.findByUsername(username);
        if(user == null) throw new UsernameNotFoundException("User not found");
        return user;
    }

    public List<User> allUsers()
    {
        return userRepository.findAll();
    }

    public Long getLastUserId()
    {
        String SQL = "(SELECT MAX(id) FROM t_user)";
        Query query = em.createNativeQuery(SQL);
        return ((BigInteger)query.getSingleResult()).longValue();
    }

    public boolean deleteUser(Long userId)
    {
        if (userRepository.findById(userId).isPresent())
        {
            userRepository.deleteById(userId);
            return true;
        }
        return false;
    }

    private List<ObjectError> ManualUserValidation(User user)
    {
        List<ObjectError> checkResult = checkForMatches(user);

        if(user.getPassword().length() < 8 || user.getPassword().length() > 40)
            checkResult.add(new ObjectError
            (
                    "password",
                    "Password length must be between 8 and 40 letters"
            ));

        return checkResult;
    }

    private List<ObjectError> checkForMatches(User newUser)
    {
        List<ObjectError> errors = new ArrayList<>();
        boolean emailExist = false;
        boolean usernameExist = false;

        String SQL = "SELECT * FROM t_user WHERE email = ? OR username = ?";
        String email = newUser.getEmail();
        String username = newUser.getUsername();

        Query query = em.createNativeQuery(SQL, User.class);
        query.setParameter(1, email);
        query.setParameter(2, username);

        @SuppressWarnings("unchecked")
        List<User> users = query.getResultList();

        for(User dbUser : users)
        {
            if(dbUser.getEmail().equals(email)) emailExist = true;
            if(dbUser.getUsername().equals(username)) usernameExist = true;
            if(emailExist && usernameExist) break;
        }

        if(emailExist)
            errors.add(new ObjectError("email", "Email " + email + " already exists"));
        if(usernameExist)
            errors.add(new ObjectError("username", "Username " + username + " already exists"));

        return errors;
    }
}