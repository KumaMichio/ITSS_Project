package services;

import exceptions.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import models.Order;
import models.Product;
import models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import repositories.OrderRepository;
import repositories.ProductRepository;
import repositories.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
public class UserService {
    private final UserRepository userRepository;

    private final OrderRepository orderRepository;

    private final ProductRepository productRepository;

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .filter(user -> !user.getRole().equals("admin"))
                .toList();
    }

    public User getUserById(int id) {
        return userRepository.findById(id).orElse(null);
    }

    public User updateUser(int userId, User updatedData) {
        User existingUser = getUserOrThrow(userId);

        if (updatedData.getUsername() != null) {
            existingUser.setUsername(updatedData.getUsername());
        }
        if (updatedData.getEmail() != null) {
            existingUser.setEmail(updatedData.getEmail());
        }
        if (updatedData.getRole() != null) {
            existingUser.setRole(updatedData.getRole());
        }

        return userRepository.save(existingUser);
    }

    @Transactional
    public User changeUserRole(int userId, String newRole) {
        User user = getUserOrThrow(userId);

        boolean hasOrders = orderRepository.findAll().stream()
                .anyMatch(order -> order.getUserID().getUser_id() == userId);
        if (hasOrders) {
            throw new IllegalStateException("Cannot change role. User has existing orders.");
        }

        boolean hasProducts = productRepository.findAll().stream()
                .anyMatch(product -> product.getSellerId().getUser_id() == userId);
        if (hasProducts) {
            throw new IllegalStateException("Cannot change role. User has listed products.");
        }

        user.setRole(newRole);
        return userRepository.save(user);
    }

    private User getUserOrThrow(int id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));
    }

//    public User updateUser(int userId, User user) throws UsernameNotFoundException {
//        Optional<User> userOptional = userRepository.findById(userId);
//        if (userOptional.isEmpty()) {
//            throw new UsernameNotFoundException("User not found with id: " + userId);
//        }
//        User existingUser = userOptional.get();
//
//        if (user.getUsername() != null) {
//            existingUser.setUsername(user.getUsername());
//        }
//        if (user.getEmail() != null) {
//            existingUser.setEmail(user.getEmail());
//        }
//        if (user.getRole() != null) {
//            existingUser.setRole(user.getRole());
//        }
//
//
//        return userRepository.save(existingUser);
//    }

//    public void deleteUser(int id) throws UsernameNotFoundException {
//        Optional<User> userOptional = userRepository.findById(id);
//        if (userOptional.isEmpty()) {
//            throw new UsernameNotFoundException("User not found with id: " + id);
//        }
//        userRepository.deleteById(id);
//    }

//    @Transactional
//    public User changeUserRole(int userId, String newRole) {
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
//        List<Order>orders = orderRepository.findAll().stream().filter(order -> order.getUserId() == userId).toList();
//        if(!orders.isEmpty()){
//            return null;
//        }
//        List<Product> products = productsRepository.findAll().stream().filter(product -> product.getSellerId() == userId).toList();
//        if(!products.isEmpty()){
//            return null;
//        }
//        user.setRole(newRole);
//        return userRepository.save(user);
//    }
}
