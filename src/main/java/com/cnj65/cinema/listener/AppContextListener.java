package com.cnj65.cinema.listener;

import com.cnj65.cinema.util.Constants;

import com.cnj65.cinema.config.DBContext;
import com.cnj65.cinema.dao.ShowtimeSeatDAO;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Khoi dong cung luc voi ung dung web (deployment).
 * Chay 1 tac vu dinh ky (moi 1 phut) de tu dong NHA CAC GHE BI GIU QUA LAU
 * ma khach chua thanh toan xong (mac dinh 5 phut - cau hinh trong db.properties).
 *
 * Day la giai phap cho van de: "khach chon ghe roi bo do khong thanh toan"
 * se khien ghe do bi "ket" mai mai neu khong co co che nay.
 */
@WebListener
public class AppContextListener implements ServletContextListener {

    private ScheduledExecutorService scheduler;
    private static final int TIMEOUT_MINUTES = Constants.SEAT_LOCK_TIMEOUT_MINUTES;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ShowtimeSeatDAO dao = new ShowtimeSeatDAO();
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            try {
                int released = dao.releaseExpiredLocks(TIMEOUT_MINUTES);
                if (released > 0) {
                    System.out.println("[SeatLockCleaner] Da tu dong nha " + released + " ghe het han giu.");
                }
            } catch (Exception e) {
                System.err.println("[SeatLockCleaner] Loi: " + e.getMessage());
            }
        }, 1, 1, TimeUnit.MINUTES);

        System.out.println("=== CINEMA CNJ65: He thong da khoi dong, SeatLockCleaner dang chay ===");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (scheduler != null) scheduler.shutdownNow();
        DBContext.closeDataSource();
        System.out.println("=== CINEMA CNJ65: He thong da dung ===");
    }
}
