package ph.edu.dlsu.lbycpob.animoquest.service;

import org.springframework.stereotype.Service;
import ph.edu.dlsu.lbycpob.animoquest.model.User;

@Service
public class SessionService {

    private User currentUser;

    // ============================================================
    // LOGIN
    // ============================================================

    public void login(User user) {

        this.currentUser = user;
    }

    // ============================================================
    // GET CURRENT USER
    // ============================================================

    public User getCurrentUser() {

        return currentUser;
    }

    // ============================================================
    // CHECK LOGIN
    // ============================================================

    public boolean isLoggedIn() {

        return currentUser != null;
    }

    // ============================================================
    // LOGOUT
    // ============================================================

    public void logout() {

        currentUser = null;
    }
}