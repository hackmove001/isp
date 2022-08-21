package by.aab.isp.web.command.customer;

import by.aab.isp.entity.Customer;
import by.aab.isp.entity.Employee;
import by.aab.isp.entity.User;
import by.aab.isp.service.UserService;
import by.aab.isp.web.command.Command;
import jakarta.servlet.http.HttpServletRequest;

public class ManageCustomersCommand extends Command {

    private final UserService userService;

    public ManageCustomersCommand(UserService userService) {
        this.userService = userService;
    }

    @Override
    public String execute(HttpServletRequest req) {
        Iterable<Customer> customers = userService.getAllCustomers();
        if (customers.spliterator().estimateSize() > 0) {
            req.setAttribute("customers", customers);
        }
        return "jsp/manage-customers.jsp";
    }

    @Override
    public boolean isAllowedForUser(User user) {
        return user instanceof Employee;
    }
}