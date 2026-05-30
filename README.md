# Reminder Notes

Kotlin notes app with Room, coroutines, exact alarms, and notifications.

## Stack

- **Target SDK:** 35 (Android 15) — meets [Google Play target API requirements](https://developer.android.com/google/play/requirements/target-sdk) for new apps and updates in 2025–2026
- **Min SDK:** 24
- **AGP:** 8.7.3 · **Gradle:** 8.11.1 · **Kotlin:** 2.0.21
- **Room:** KSP · **UI:** View Binding (no Data Binding / kapt)

## Build

```bash
./gradlew assembleDebug
./gradlew assembleRelease
```

Open the project in Android Studio Ladybug (2024.2+) or newer.

## Play Store checklist (manual steps)

1. **Target API 35** — configured in `app/build.gradle`
2. **Notifications** — `POST_NOTIFICATIONS` requested at runtime on Android 13+
3. **Exact alarms** — `SCHEDULE_EXACT_ALARM`; user may need to allow “Alarms & reminders” in system settings on Android 12+
4. **Boot / update** — `BootReceiver` reschedules future reminders after reboot or app update (`BOOT_COMPLETED`, `MY_PACKAGE_REPLACED`)
5. **Data safety** — declare that data stays on-device (Room SQLite); no account or network collection
6. **Privacy policy** — required in Play Console even if you only store local notes; host a short policy URL
7. **App signing** — use Play App Signing with an upload key
8. **16 KB page size** — no native `.so` libraries; JVM-only app is unaffected by the Nov 2025 native alignment rule

## Permissions

| Permission | Purpose |
|------------|---------|
| `POST_NOTIFICATIONS` | Show reminder notifications |
| `SCHEDULE_EXACT_ALARM` | Fire reminders at the chosen date/time |
| `RECEIVE_BOOT_COMPLETED` | Reschedule reminders after device reboot |
