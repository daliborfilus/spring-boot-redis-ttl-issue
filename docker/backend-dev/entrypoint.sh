#!/usr/bin/env bash
case "$1" in
    bootRun)
        echo "chown-ing volumes to user app..."
        # chown the directory
        chown app:app -R /opt/backend/.gradle
        chown app:app -R /opt/.gradle
        echo "running ./gradlew bootRun as user app..."
        exec su -p -c './gradlew bootRun --no-daemon' app
        ;;
    *)
        exec "$@"
        ;;
esac
