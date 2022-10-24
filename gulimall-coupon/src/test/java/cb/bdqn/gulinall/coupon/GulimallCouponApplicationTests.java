package cb.bdqn.gulinall.coupon;

import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class GulimallCouponApplicationTests {

    @Test
    public void contextLoads() {

        LocalDate now = LocalDate.now(); // 现在的时间

        LocalDate localDate = now.plusDays(2); // 2 天以后的时间

        System.out.println(now+"date"+localDate);

        LocalTime max = LocalTime.MAX; // 23:59:59.999999999
        LocalTime min = LocalTime.MIN; // 00:00
        System.out.println(max);
        System.out.println(min);

        LocalDateTime start = LocalDateTime.of(now, min);
        LocalDateTime end = LocalDateTime.of(localDate, max);
        String format = start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        System.out.println(format+"\t"+end);

    }

}
