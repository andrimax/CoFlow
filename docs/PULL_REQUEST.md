# Порядок действий

## Коммиты

Формат:
```text
тип(тг#номер_задачи): что сделал
```

Типы: `feat`, `fix`, `docs`, `refactor`, `test`, `chore`, `style`, `perf`.

Правила:
- Прошедшее время: «добавил», «исправил», «вынес».
- После заголовка — пустая строка, затем описание.
- Мелкие правки — без номера задачи: `docs: опечатка в README`.

## Ветки

- `main` — продакшн, прямые коммиты запрещены.
- `feature/*` — новый функционал.
- `fix/*` — исправление бага.

## Пайплайн

1. Создать ветку от актуального `main`:
```bash
git checkout main
git pull origin main
git checkout -b feature/auth-jwt
```

2. Коммитить в ветке (`wip`, `fixed typo` — любые названия).

3. При необходимости синхронизироваться с `main`:
```bash
git fetch origin
git rebase origin/main
```

4. Отправить ветку:
```bash
git push -u origin feature/auth-jwt
```

5. Открыть Pull Request из `feature/auth-jwt` в `main`.

6. Провести код-ревью.

7. Выбрать **Squash and merge**.

8. Итоговый коммит оформить по Conventional Commits:
```text
feat(auth): add JWT-based authentication middleware
```

9. Удалить ветку после слияния.