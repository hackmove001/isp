package by.aab.isp.web.command.tariff;

import by.aab.isp.entity.Employee;
import by.aab.isp.entity.Tariff;
import by.aab.isp.entity.User;
import by.aab.isp.service.Pagination;
import by.aab.isp.service.TariffService;
import by.aab.isp.web.Util;
import jakarta.servlet.http.HttpServletRequest;

import static by.aab.isp.web.Const.DEFAULT_PAGE_SIZE;

public class ManageTariffsCommand extends by.aab.isp.web.command.Command {

    private final TariffService tariffService;

    public ManageTariffsCommand(TariffService tariffService) {
        this.tariffService = tariffService;
    }

    @Override
    public String execute(HttpServletRequest req) {
        Pagination pagination = new Pagination();
        pagination.setPageSize(DEFAULT_PAGE_SIZE);
        String page = req.getParameter("page");
        pagination.setPageNumber(page != null ? Integer.parseInt(page)
                                              : 0);
        Iterable<Tariff> tariffs = tariffService.getAll(pagination);
        if (tariffs.spliterator().estimateSize() > 0) {
            req.setAttribute("tariffs", tariffs);
            req.setAttribute("util", Util.getInstance());
        }
        req.setAttribute("pagination", pagination);
        return "jsp/manage-tariffs.jsp";
    }

    @Override
    public boolean isAllowedForUser(User user) {
        return user instanceof Employee;
    }
}
