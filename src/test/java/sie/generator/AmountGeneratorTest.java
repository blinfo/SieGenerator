package sie.generator;

import java.math.BigDecimal;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static sie.generator.Generator.*;

/**
 *
 * @author hakan
 */
public class AmountGeneratorTest {

    @Test
    public void test_randomAmount() {
        BigDecimal randomAmount = AmountGenerator.randomAmount();
        assertNotNull(randomAmount);
        assertEquals(2, randomAmount.scale());
    }

    @Test
    public void test_createVoucherAmount() {
        AmountGenerator.VoucherAmount voucherAmount = AmountGenerator.createVoucherAmount();
        BigDecimal b = voucherAmount.vatAmount().abs().add(voucherAmount.restAmount().abs());
        assertEquals(voucherAmount.totalAmount(), b);
        assertEquals(voucherAmount.totalAmount().multiply(voucherAmount.vatMultiplier()).setScale(ROUNDING_SCALE, ROUNDING_MODE).negate(), voucherAmount.vatAmount());
    }
}
