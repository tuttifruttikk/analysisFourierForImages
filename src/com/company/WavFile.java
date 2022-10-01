package com.company;

import java.io.*;

public class WavFile
{
	private final static int BUFFER_SIZE = 4096;
	private final static int FMT_CHUNK_ID = 0x20746D66;
	private final static int DATA_CHUNK_ID = 0x61746164;

	private File file;						// File that will be read from or written to
	private int bytesPerSample;			// Number of bytes required to store a single sample
	private long numFrames;					// Number of frames within the data section
	private FileOutputStream oStream;	// Output stream used for writting data
	private FileInputStream iStream;		// Input stream used for reading data
	private double floatScale;				// Scaling factor used for int <-> float conversion
	private double floatOffset;			// Offset factor used for int <-> float conversion

	private int numChannels;				// 2 bytes unsigned, 0x0001 (1) to 0xFFFF (65,535)
	private long sampleRate;				// 4 bytes unsigned, 0x00000001 (1) to 0xFFFFFFFF (4,294,967,295)
													// Although a java int is 4 bytes, it is signed, so need to use a long
	private int blockAlign;					// 2 bytes unsigned, 0x0001 (1) to 0xFFFF (65,535)
	private int validBits;					// 2 bytes unsigned, 0x0002 (2) to 0xFFFF (65,535)


	private byte[] buffer;					// Local buffer used for IO
	private int bufferPointer;				// Points to the current position in local buffer
	private int bytesRead;					// Bytes read after last read into local buffer
	private long frameCounter;				// Current number of frames read or written

	private WavFile()
	{
		buffer = new byte[BUFFER_SIZE];
	}


	public long getFramesRemaining()
	{
		return numFrames - frameCounter;
	}


	public static WavFile openWavFile(File file) throws IOException, RuntimeException
	{
		WavFile wavFile = new WavFile();
		wavFile.file = file;
		wavFile.iStream = new FileInputStream(file);

		int bytesRead = wavFile.iStream.read(wavFile.buffer, 0, 12);
		long riffChunkID = getLE(wavFile.buffer, 0, 4);
		long chunkSize ;
		long riffTypeID = getLE(wavFile.buffer, 8, 4);

		boolean foundFormat = false;
		boolean foundData ;

		while (true) {
			bytesRead = wavFile.iStream.read(wavFile.buffer, 0, 8);
			long chunkID = getLE(wavFile.buffer, 0, 4);
			chunkSize = getLE(wavFile.buffer, 4, 4);
			long numChunkBytes = (chunkSize % 2 == 1) ? chunkSize + 1 : chunkSize;

			if (chunkID == FMT_CHUNK_ID) {
				foundFormat = true;
				bytesRead = wavFile.iStream.read(wavFile.buffer, 0, 16);
				int compressionCode = (int) getLE(wavFile.buffer, 0, 2);
				wavFile.numChannels = (int) getLE(wavFile.buffer, 2, 2);
				wavFile.sampleRate = getLE(wavFile.buffer, 4, 4);
				wavFile.blockAlign = (int) getLE(wavFile.buffer, 12, 2);
				wavFile.validBits = (int) getLE(wavFile.buffer, 14, 2);
				wavFile.bytesPerSample = (wavFile.validBits + 7) / 8;

				numChunkBytes -= 16;
				if (numChunkBytes > 0) {
					wavFile.iStream.skip(numChunkBytes);
				}
			} else if (chunkID == DATA_CHUNK_ID) {
				wavFile.numFrames = chunkSize / wavFile.blockAlign;
				foundData = true;
				break;
			} else {
				wavFile.iStream.skip(numChunkBytes);
			}
		}

		if (foundData == false) throw new RuntimeException("Did not find a data chunk");
		if (wavFile.validBits > 8) {
			wavFile.floatOffset = 0;
			wavFile.floatScale = 1 << (wavFile.validBits - 1);
		} else {
			wavFile.floatOffset = -1;
			wavFile.floatScale = 0.5 * ((1 << wavFile.validBits) - 1);
		}
		wavFile.bufferPointer = 0;
		wavFile.bytesRead = 0;
		wavFile.frameCounter = 0;

		return wavFile;
	}


	private static long getLE(byte[] buffer, int pos, int numBytes) {
		numBytes --;
		pos += numBytes;
		long val = buffer[pos] & 0xFF;
		for (int b = 0 ; b < numBytes ; b++) {
			val = (val << 8) + (buffer[--pos] & 0xFF);
		}
		return val;
	}

	private long readSample() throws IOException, RuntimeException {
		long val = 0;
		for (int b = 0 ; b < bytesPerSample ; b++) {
			if (bufferPointer == bytesRead) {
				int read = iStream.read(buffer, 0, BUFFER_SIZE);
				if (read == -1) throw new RuntimeException("Not enough data available");
				bytesRead = read;
				bufferPointer = 0;
			}

			int v = buffer[bufferPointer];
			if (b < bytesPerSample-1 || bytesPerSample == 1) v &= 0xFF;
			val += v << (b * 8);

			bufferPointer ++;
		}
		return val;
	}

	public int readFrames(double[] sampleBuffer, int numFramesToRead) throws IOException, RuntimeException {
		return readFrames(sampleBuffer, 0, numFramesToRead);
	}

	public int readFrames(double[] sampleBuffer, int offset, int numFramesToRead) throws IOException, RuntimeException {
		for (int f = 0 ; f < numFramesToRead ; f++) {
			if (frameCounter == numFrames) {
				return f;
			}
			for (int c = 0 ; c < numChannels ; c++) {
				sampleBuffer[offset] = floatOffset + (double) readSample() / floatScale;
				offset++;
			}
			frameCounter++;
		}
		return numFramesToRead;
	}
}