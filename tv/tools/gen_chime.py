#!/usr/bin/env python3
import math
import struct
import sys
import wave

SAMPLE_RATE = 22050
DURATION_S = 0.3
START_HZ = 440.0
END_HZ = 880.0
AMPLITUDE = 0.6
FADE_S = 0.01


def sample(i, total):
    t = i / SAMPLE_RATE
    progress = i / total
    frequency = START_HZ + (END_HZ - START_HZ) * progress
    phase = 2 * math.pi * (START_HZ * t + (END_HZ - START_HZ) * t * t / (2 * DURATION_S))
    fade_in = min(1.0, t / FADE_S)
    fade_out = min(1.0, (DURATION_S - t) / FADE_S)
    return AMPLITUDE * math.sin(phase) * min(fade_in, fade_out)


def main(path):
    total = int(SAMPLE_RATE * DURATION_S)
    frames = b"".join(struct.pack("<h", int(sample(i, total) * 32767)) for i in range(total))
    with wave.open(path, "wb") as out:
        out.setnchannels(1)
        out.setsampwidth(2)
        out.setframerate(SAMPLE_RATE)
        out.writeframes(frames)


if __name__ == "__main__":
    main(sys.argv[1] if len(sys.argv) > 1 else "app/src/main/res/raw/chime.wav")
