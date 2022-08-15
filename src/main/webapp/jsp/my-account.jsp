<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
%><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"
%><!doctype html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>My account | Internet Service Provider</title>
    </head>
    <body>
<%@ include file="inc/page-header.inc" %>
        <main>
            <h1>My account</h1>
            <c:choose>
                <c:when test="${activeCustomer != null}">
                    <ul>
                        <li>
                            <p${activeCustomer.balance < 0 ? ' class="negative"' : ''}>Balance: ${activeCustomer.balance}
                            <form action="?action=replenish_balance" method="POST">
                                <label for="amount">Replenish balance by:</label>
                                <input name="amount" type="number" required min="0.01" step="0.01">
                                <input type="submit" value="Replenish">
                            </form>
                        </li>
                        <li>
                            <p>My subscriptions:
                            <table>
                                <tr>
                                    <th>Since</th>
                                    <th>Until</th>
                                    <th>Tariff</th>
                                    <th>Price</th>
                                    <th>Bandwidth</th>
                                    <th>Traffic consumed</th>
                                    <th>Traffic left</th>
                                    <th></th>
                                </tr>
                                <c:if test="${subscriptions == null}">
                                    <tr><td colspan=8>No subscriptions</td></tr>
                                </c:if>
                                <c:forEach var="subscription" items="${subscriptions}">
                                    <tr${subscription.activeUntil == null || subscription.activeUntil > now ? ' class="active"' : ''}>
                                        <td>${subscription.activeSince}</td>
                                        <td>${subscription.activeUntil != null ? subscription.activeUntil : 'Until cancelled'}</td>
                                        <td><c:out value="${subscription.tariff.name}" /></td>
                                        <td>${subscription.price}</td>
                                        <td></td>
                                        <td></td>
                                        <td></td>
                                        <td><c:if test="${subscription.activeUntil == null || subscription.activeUntil > now}">
                                            <a href="?action=cancel_subscription&subscription_id=${subscription.id}">Cancel</a></c:if>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </table>
                            <c:if test="${tariffs != null}">
                                <form action="?action=subscribe" method="POST">
                                    <label for="new-tariff">Subscribe to another tariff:</label>
                                    <select name="new-tariff" required>
                                        <c:forEach var="tariff" items="${tariffs}">
                                            <option value="${tariff.id}"><c:out value="${tariff.name}" /></option>
                                        </c:forEach>
                                    </select>
                                    <input type="submit" value="Subscribe">
                                </form>
                            </c:if>
                        </li>
                    </ul>
                </c:when>
                <c:when test="${activeEmployee != null}">
                    <ul><li>Role: ${activeEmployee.role.name().toLowerCase()}</ul>
                </c:when>
            </c:choose>
            <form action="?action=update_my_credentials" method="POST">
                <ul>
                    <li>
                        <label for="email">Email:</label>
                        <input name="email" type="email" maxlength=25
                            value="<c:out value="${activeUser.email}" />">
                    </li>
                    <li>
                        <label for="new-password1">New password:</label>
                        <input name="new-password1" type="password">
                    </li>
                    <li>
                        <label for="new-password2">Confirm new password:</label>
                        <input name="new-password2" type="password">
                    </li>
                    <li>
                        <label for="current-password">Current password:</label>
                        <input name="current-password" type="password" required>
                    </li>
                    <input type="submit" value="Update">
                </ul>
            </form>
        </main>
    </body>
</html>