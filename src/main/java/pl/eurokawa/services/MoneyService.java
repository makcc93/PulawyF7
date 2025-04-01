package pl.eurokawa.services;

import com.vaadin.flow.component.notification.NotificationVariant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.eurokawa.data.Money;
import pl.eurokawa.data.MoneyRepository;
import pl.eurokawa.data.User;
import pl.eurokawa.data.UserRepository;

import  com.vaadin.flow.component.notification.Notification;

import java.util.List;

@Service
public class MoneyService {

    MoneyRepository moneyRepository;
    UserRepository userRepository;
    double currentBalance;

    public MoneyService(MoneyRepository moneyRepository, UserRepository userRepository) {
        this.moneyRepository = moneyRepository;
        this.userRepository = userRepository;

        currentBalance = moneyRepository.findLatestBalance().map(Money::getBalance).orElse(0.00);

//        currentBalance = moneyRepository.findLatestBalance().map(Money::getBalance).orElse(0.00);

//    public double getCurrentBalance(){
//        return moneyRepository.findLatestBalance()
//                .map(Money::getBalance)
//                .orElse(0.00);
//    }
    }

    private synchronized double test(MoneyRepository moneyRepository){
        return currentBalance = moneyRepository.findLatestBalance().map(Money::getBalance).orElse(0.00);
    }

    public synchronized double getCurrentBalance(){
        return currentBalance;
    }

    public synchronized void processTransaction(double amount){
        currentBalance += amount;

        BalanceBroadcaster.broadcast(currentBalance);

    }

    public List<Money> getOrderHistory(){

        return moneyRepository.findHistoryOfUserDeposits();
    }

    @Transactional
    public Money addDeposit(User user, double deposit){
        if (deposit < 0){
            showNotification("Wartość nie może być ujemna",NotificationVariant.LUMO_ERROR);

            return null;
        }

        double currentBalance = getCurrentBalance();
        double updatedBalance = currentBalance + deposit;

        Money moneyDeposit = new Money();
        moneyDeposit.setUser(user);
        moneyDeposit.setDeposit(deposit);
        moneyDeposit.setBalance(updatedBalance);

        BalanceBroadcaster.broadcast(updatedBalance);

        return moneyRepository.save(moneyDeposit);
    }

    @Transactional
    public Money addWithdrawal(User user, double withdrawal){
        if (withdrawal < 0){
            showNotification("Wartość nie może być ujemna",NotificationVariant.LUMO_ERROR);

            return null;
        }

        double currentBalance = moneyRepository.findLatestBalance().map(Money::getBalance).orElse(0.00);
        double updatedBalance = currentBalance - withdrawal;

        Money moneyWithdrawal = new Money();
        moneyWithdrawal.setUser(user);
        moneyWithdrawal.setWithdrawal(withdrawal);
        moneyWithdrawal.setBalance(updatedBalance);

        BalanceBroadcaster.broadcast(updatedBalance);


        return moneyRepository.save(moneyWithdrawal);
    }

    private void showNotification(String message, NotificationVariant notificationVariant){
        Notification notification = Notification.show(message);
        notification.addThemeVariants(notificationVariant);
        notification.setPosition(Notification.Position.BOTTOM_CENTER);
        notification.setDuration(1000);
    }
}
