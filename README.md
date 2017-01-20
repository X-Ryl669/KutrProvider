# KutrProvider
An account manager for [**Kutr**](https://github.com/X-Ryl669/kutr) on Android.

This code contains the expected account handling for [**Kutr**](https://github.com/X-Ryl669/kutr)'s servers on Android (accessible via the `Settings`/`Accounts` menu).

It also provides a service that can be used to get all the informations from your music library.

It deals with cache (so you don't have to download your songs each time you want to play them) and structuring the data format so 
it's usable with usual players.

The service itself can be used by any music player application (it's player-agnostic), some glue code must be written for the player.
A plugin for [Orpheus music player](https://github.com/X-Ryl669/Orpheus) is being written at the same time.

Currently, there is no reason it would not work with Koel's server too, except for the additional features found in [**Kutr**](https://github.com/X-Ryl669/kutr) (like genre and folder browsing).
