# OverPitch

This library is an implementation of the STFT phase vocoder algorithm used to change pitch or duration of audio without affecting the other.

This fork has been modified to run in a Windows environment. The `scsynth.exe` binary has been included and the code has been modified to connect to the external server and use the appropriate functions.

The elephant in the room here is Overtone, and the SuperCollider engine, and the fuss of hooking it all up just for the audio file read/write operations. I believe it could be replaced with a few calls to Java SampledSound class, see e.g. Dragan Djuric's [Clojure Sound](https://github.com/uncomplicate/clojure-sound) wrapper library, [Dynne](https://github.com/candera/dynne)

## Requirements
This project is written in Clojure, and the dependencies are listed in
`project.clj`. The simplest way to use this project is to install
[Leiningen](https://leiningen.org/) and run the command `lein deps`.

## Usage

Start the SuperCollider server:

``` powershell
./scsynth.exe -u 57110

...
SuperCollider 3 server ready.
```

Given a the file `your-audio-file.wav` file, you can pitch-shift it by a factor
1.2 by calling either:

```clj
(-main "your-audio-file.wav" "output.wav" 1.2)
```

from inside the project repl (by running `lein repl`), or by running

```bash
lein run "your-audio-file.wav" "output.wav" 1.2
```

from the command line.

The result will be written in `output.wav`.

## Documentation
An in-depth theoretical explanation of the process involved in the
pitch-shifting algorithm implemented by this library is available in the folder
`docs/`.

## License
Distributed under the Eclipse Public License either version 1.0 or any later
version.
