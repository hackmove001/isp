<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
%><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"
%><!doctype html>
<html>
    <head>
        <meta charset="UTF-8">
        <title><c:out value="${customer.email}" />${customer.id !=0 ? ' | Edit' : 'New'} customer account | Internet Service Provider</title>
    </head>
    <body>
<%@ include file="inc/page-header.inc" %>
        <main>
            <h1>${customer.id !=0 ? 'Edit' : 'New'} customer account</h1>
            <form action="?action=save_customer" method="POST">
                <input type="hidden" name="id" value="${customer.id}">
                <ul>
                    <li>
                        <label for="email">Email:</label>
                        <input type="email" name="email" required maxlength=25 placeholder="user@example.com"
                            value="<c:out value="${customer.email}" />">
                    </li>
                    <li>
                        <label for="balance">Balance:</label>
                        <input name="balance" type="number" required step="0.01" value="${customer.balance}">
                    </li>
                    <li>
                        <label for="permitted-overdraft">Permitted overdraft:</label>
                        <input name="permitted-overdraft" type="number" required min="0" step="0.01" value=${customer.permittedOverdraft}>
                    </li>
                    <li>
                        <label for="payoff-date">Payoff date:</label>
                        <input name="payoff-date" type="date" value=${payoffDate}>
                    </li>
                    <li>
                        <label for="tariff">Tariff:</label>
                        <select name="tariff">
                            <option value="0"${activeTariff == null ? ' selected' : ''}>no</option><c:forEach var="tariff" items="${tariffs}">
                            <option value="${tariff.id}"${activeTariff.id == tariff.id ? ' selected' : ''}><c:out value="${tariff.name}" /></option>
                        </c:forEach></select>
                    </li>
                </ul>
                <input type="submit" value="Submit">
            </form>
        </main>
    </body>
</html>