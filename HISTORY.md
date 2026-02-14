**15.02.2026**

- сделан третий микросервис PostUserApp
- создана база в postres, спроектированы сущности юзера
- при помощи датафакера залиты тестовые данные на 200к записей (планируется до 170 млн)
- установлен DBeaver, установлен индекс на uuid
- в PostRegistrationApp добавлен асинхронный запрос в PostUserApp для валидации юзера
- протестирована вся цепочка PostApp - PostRegistrationApp - PostUserApp, flow в рабочем состоянии

**14.02.2026**

- JMeter, устранена ошибка по заниманию лишних клиентских портов, 800 rps проходит без проблем
- Добавлен gc.log, разобран и G1 затюнен, чтобы не было лишних лагов на 109 мс
- Добавлены prometheus и grafana, в prometheus подключен rabbitmq плагин, в grafana настроены алерты
на consumer service, unack message и ready messages > threshold
- Настроены contact points и алерты в телеграм

**13.02.2026**

- установлен JMeter, выдает график-пилу при нагрузке
- настроен rabbitmq. на всякий случай сделан conditional-bean на kafka
- настроены confirmation и return callback-и
- создана dead letter queueu (на всякий случай)