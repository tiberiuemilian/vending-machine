package mvpmatch.vm.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static java.util.Map.entry;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VendingMachineServiceTest {

    @Test
    @DisplayName("Deposit(1X100 ; 2X20), value=70, Change(1X50; 1X20)")
    void buy1() {
        Map<Integer, Integer> initial = new HashMap<>(
                Map.ofEntries(
                        entry(100, 1),
                        entry(50, 0),
                        entry(20, 2),
                        entry(10, 0),
                        entry(5, 0)
                )
        );

        Map<Integer, Integer> expected = new HashMap<>(
                Map.ofEntries(
                        entry(100, 0),
                        entry(50, 1),
                        entry(20, 1),
                        entry(10, 0),
                        entry(5, 0)
                )
        );

        VendingMachineService.buy(initial, 70);
        assertTrue(expected.equals(initial));
    }

    @Test
    @DisplayName("Deposit(1X100 ; 2X20; 1X10), value=130, Change(1X20)")
    void buy2() {
        Map<Integer, Integer> initial = new HashMap<>(
                Map.ofEntries(
                        entry(100, 1),
                        entry(50, 0),
                        entry(20, 2),
                        entry(10, 1),
                        entry(5, 0)
                )
        );

        Map<Integer, Integer> expected = new HashMap<>(
                Map.ofEntries(
                        entry(100, 0),
                        entry(50, 0),
                        entry(20, 1),
                        entry(10, 0),
                        entry(5, 0)
                )
        );

        VendingMachineService.buy(initial, 130);
        assertTrue(expected.equals(initial));
    }

    @Test
    @DisplayName("Deposit(1X100 ; 2X20; 1X10), value=70, Change(1X50; 1X20; 1X10)")
    void buy3() {
        Map<Integer, Integer> initial = new HashMap<>(
                Map.ofEntries(
                        entry(100, 1),
                        entry(50, 0),
                        entry(20, 2),
                        entry(10, 1),
                        entry(5, 0)
                )
        );

        Map<Integer, Integer> expected = new HashMap<>(
                Map.ofEntries(
                        entry(100, 0),
                        entry(50, 1),
                        entry(20, 1),
                        entry(10, 1),
                        entry(5, 0)
                )
        );

        VendingMachineService.buy(initial, 70);
        assertTrue(expected.equals(initial));
    }

    @Test
    @DisplayName("Deposit(4X100), value=230, Change(1X100; 1X50; 1X20)")
    void buy4() {
        Map<Integer, Integer> initial = new HashMap<>(
                Map.ofEntries(
                        entry(100, 4),
                        entry(50, 0),
                        entry(20, 0),
                        entry(10, 0),
                        entry(5, 0)
                )
        );

        Map<Integer, Integer> expected = new HashMap<>(
                Map.ofEntries(
                        entry(100, 1),
                        entry(50, 1),
                        entry(20, 1),
                        entry(10, 0),
                        entry(5, 0)
                )
        );

        VendingMachineService.buy(initial, 230);
        assertTrue(expected.equals(initial));
    }

    @Test
    @DisplayName("Deposit(10X100 ; 1X10; 4X5), value=230, Change(8X100)")
    void buy5() {
        Map<Integer, Integer> initial = new HashMap<>(
                Map.ofEntries(
                        entry(100, 10),
                        entry(50, 0),
                        entry(20, 0),
                        entry(10, 1),
                        entry(5, 4)
                )
        );

        Map<Integer, Integer> expected = new HashMap<>(
                Map.ofEntries(
                        entry(100, 8),
                        entry(50, 0),
                        entry(20, 0),
                        entry(10, 0),
                        entry(5, 0)
                )
        );

        VendingMachineService.buy(initial, 230);
        assertTrue(expected.equals(initial));
    }

    @Test
    @DisplayName("Deposit(1X100; 1X10 1X5), value=115, Change(0)")
    void buy6() {
        Map<Integer, Integer> initial = new HashMap<>(
                Map.ofEntries(
                        entry(100, 1),
                        entry(50, 0),
                        entry(20, 0),
                        entry(10, 1),
                        entry(5, 1)
                )
        );

        Map<Integer, Integer> expected = new HashMap<>(
                Map.ofEntries(
                        entry(100, 0),
                        entry(50, 0),
                        entry(20, 0),
                        entry(10, 0),
                        entry(5, 0)
                )
        );

        VendingMachineService.buy(initial, 115);
        assertTrue(expected.equals(initial));
    }

    @Test
    @DisplayName("Deposit(1X100; 2X5), value=40, Change(1X50; 1X20)")
    void buy7() {
        Map<Integer, Integer> initial = new HashMap<>(
                Map.ofEntries(
                        entry(100, 1),
                        entry(50, 0),
                        entry(20, 0),
                        entry(10, 0),
                        entry(5, 2)
                )
        );

        Map<Integer, Integer> expected = new HashMap<>(
                Map.ofEntries(
                        entry(100, 0),
                        entry(50, 1),
                        entry(20, 1),
                        entry(10, 0),
                        entry(5, 0)
                )
        );

        VendingMachineService.buy(initial, 40);
        assertTrue(expected.equals(initial));
    }

}
