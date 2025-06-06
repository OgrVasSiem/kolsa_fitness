Архитектура и принятые решения
Использован Hilt для внедрения зависимостей.
UI-состояния обрабатываются через StateFlow, чтобы исключить лишнюю логику из фрагментов.

Сетевой слой
Работа с сетью реализована через Retrofit и OkHttp.
Все ошибки обрабатываются и отображаются пользователю в виде простых состояний — загрузка, ошибка, отсутствие интернета.

Отслеживание соединения
Реализован кастомный NetworkStatusTracker, который используется при загрузке данных на обоих экранах.
Если сети нет — показывается соответствующее состояние без запроса к API.

Работа с видео
Для воспроизведения видео используется ExoPlayer с поддержкой выбора качества и скорости воспроизведения.
Интерфейс управления встроен прямо в плеер.
Видео загружается по URL, полученному из API.

UI
Интерфейс написан на XML.
Список тренировок — RecyclerView.
Фильтрация по типу и поиск реализованы с помощью PopupMenu и EditText.
Каждая тренировка отображается с основными полями: название, тип, длительность, описание.

Общие решения
Простой и читаемый код

Отдельные классы для логики

Минимум зависимостей

Упор на работоспособность и стабильность
