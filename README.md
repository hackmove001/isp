# Интернет-провайдер

## Учебный проект

**Клиент** может просмотреть состояние своего счёта и трафика, пополнить счёт,
изменить личные параметры и тарифный план.

**Администратор** регистрирует клиентов, управляет тарифными планами
(корректирует и добавляет новые), объявляет акции, скидки и т.д., при нарушении
правил пользования системой может блокировать клиента.

## Функциональные требования

- [x] Авторизация (sign in) и выход (sign out) в/из системы.
- [ ] Хранение паролей в виде хэшей
- [x] Регистрация пользователя и/или добавление артефакта предметной области системы.
- [x] Просмотр информации (например: просмотр всех ставок тотализатора, статистики заказов, счетов и т.д.)
- [x] Удаление информации (например: отмена заказа, удаление сущности и т.д.)
- [x] Добавление и модификация информации (например: создать и отредактировать товар, создать и отредактировать заказ и
 т.д.)
- [ ] Во всех проектах, где присутствуют финансовые отношения, возможно использование оплаты в кредит
- [ ] Интерфейс приложения должен быть локализован; выбор из языков: EN|BE|RU и т.д.

### Клиент

- [x] Просматривает состояние своего счёта и трафика
- [x] Пополняет счёт
- [x] Изменяет свои личные параметры и тарифный план

### Менеджер

- [x] Управляет клиентами (регистрирует, блокирует и разблокирует)
- [x] Управляет тарифными планами (корректирует и добавляет новые)
- [x] Редактирует заглавную страницу: объявляет акции, скидки и т.д.
- [x] Изменяет свои личные параметры

### Администратор

- [x] Имеет весь функционал менеджера
- [x] Управляет менеджерами и администраторами (регистрирует, блокирует и разблокирует, изменяет роли)
- [x] Нельзя удалить или разжаловать администратора, если он последний активный в системе

### Незарегистрированный пользователь

- [x] Просматривает информацию об актуальных тарифных планах и скидках
- [ ] Отправляет заявку на подключение

## Нефункциональные требования

- [x] Приложение реализовать, применяя технологии Servlet и JSP
- [x] Архитектура приложения должна соответствовать шаблонам Layered architecture и MVC. Controller может быть только
 ~~двух видов: контроллер роли или контроллер приложения~~ один.
- [x] Информация о предметной области должна хранится в БД
- [x] Если данные в базе хранятся на кириллице, рекомендуется применять кодировку UTF-8
- [x] Технология доступа к БД **только** JDBC
- [x] Параметры соединения с БД читаются из файла конфигурации
- [x] Для работы с БД в приложении должен быть реализован потокобезопасный пул соединений, использовать *synchronized* и
 *volatile* запрещено
- [x] При проектировании БД рекомендуется использовать не более (и не менее) 6-8 таблиц
- [x] Работу с данными в приложении осуществлять посредством шаблона DAO
- [x] Приложение должно корректно обрабатывать возникающие исключительные ситуации, в том числе вести их лог. В качестве
 логгера использовать Log4J2/SLF4J
- [x] Классы и другие сущности приложения должны быть грамотно структурированы по пакетам и иметь отражающую их
 функциональность название
- [x] При реализации бизнес-логики приложения следует при необходимости использовать шаблоны проектирования (например,
 шаблоны GoF: Factory Method, Command, Builder, Strategy, State, Observer, Singleton, Proxy и т.д.
- [x] Для хранения пользовательской информации между запросами использовать сессию
- [x] Для перехвата и корректировки объектов запроса (request) и ответа (response) применить фильтры
- [ ] Разрешается, но не обязательно использовать технологии AspectJ и Web-services
- [x] При реализации страниц JSP следует использовать теги библиотеки JSTL, использовать скриптлеты запрещено
- [x] При реализации пользовательского интерфейса разрешается использовать любые технологии front-end разработки 
 (JavaScript, AJAX и т.д.)
- [x] Реализовать защиту от повторного выполнения запроса нажатием F5
- [x] Реализовать защиту от HTML и JavaScript Injection
- [ ] Просмотр длинных списков желательно организовывать в постраничном режиме
- [ ] Валидацию входных данных производить и на клиенте, и на сервере
- [ ] Документацию к проекту необходимо оформить согласно требованиям javadoc
- [x] Оформление кода должно соответствовать Java Code Convention
- [x] При развертывании приложения разрешается использовать технологию Maven
- [ ] Приложение должно содержать JUnit
- [x] Приложение должно быть размещено на удалённом git-репозитории