# Curious Catalog Gatherer

This app is in alpha phase and will most likely stay that way for a long time.

An Android application to recognize Magic: The Gathering cards.

Uses Google's Mobile Vision API for text recognition.

Planned functionality is now there but error handling and prettying up the UI and code is not yet done.

## Good to know
   * Uses a third party catalog service to query card owners with owned amounts
   * Uses a third party catalog service to manage own cards

## Deployment to Google Play

In Android Studio build the app bundle: Build > Generate Signed Bundle / APK... > Android App Bundle > Next ...

Built bundle is located at `app/release/app.aab`.

Then in Google Play console create new relese: https://play.google.com/apps/publish

## License

Copyright © 2017 Ari Paasonen

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
