package org.vosk.demo

import java.io.File
import kotlin.experimental.and

private fun short2byte(sData: ShortArray): ByteArray {
    val arrSize = sData.size
    val bytes = ByteArray(arrSize * 2)
    for (i in 0 until arrSize) {
        bytes[i * 2] = (sData[i] and 0x00FF).toByte()
        bytes[i * 2 + 1] = (sData[i].toInt() shr 8).toByte()
        sData[i] = 0
    }
    return bytes
}

/**
 * Creates data buffer with WAV file header
 */
fun createData(): MutableList<Byte> {
    return buildList {
        for (byte in wavFileHeader())
            add(byte)
    }.toMutableList()
}

/**
 * Add buffer data to our WAV file
 */
fun addBufferData(data: ArrayList<Byte>, buffer: ShortArray) {
    val bData = short2byte(buffer)
    for (byte in bData)
        data.add(byte)
}

/**
 * Write WAV data to disk
 */
fun writeFile(data: ArrayList<Byte>, file: File) {
    updateHeaderInformation(data)
    file.writeBytes(data.toByteArray())
}

/**
 * Constructs header for wav file format
 */
private fun wavFileHeader(): ByteArray {
    val headerSize = 44
    val header = ByteArray(headerSize)

    header[0] = 'R'.toByte() // RIFF/WAVE header
    header[1] = 'I'.toByte()
    header[2] = 'F'.toByte()
    header[3] = 'F'.toByte()

    header[4] = (0 and 0xff).toByte() // Size of the overall file, 0 because unknown
    header[5] = (0 shr 8 and 0xff).toByte()
    header[6] = (0 shr 16 and 0xff).toByte()
    header[7] = (0 shr 24 and 0xff).toByte()

    header[8] = 'W'.toByte()
    header[9] = 'A'.toByte()
    header[10] = 'V'.toByte()
    header[11] = 'E'.toByte()

    header[12] = 'f'.toByte() // 'fmt ' chunk
    header[13] = 'm'.toByte()
    header[14] = 't'.toByte()
    header[15] = ' '.toByte()

    header[16] = 16 // Length of format data
    header[17] = 0
    header[18] = 0
    header[19] = 0

    header[20] = 1 // Type of format (1 is PCM)
    header[21] = 0

    header[22] = NUMBER_CHANNELS.toByte()
    header[23] = 0

    header[24] = (RECORDER_SAMPLE_RATE and 0xff).toByte() // Sampling rate
    header[25] = (RECORDER_SAMPLE_RATE shr 8 and 0xff).toByte()
    header[26] = (RECORDER_SAMPLE_RATE shr 16 and 0xff).toByte()
    header[27] = (RECORDER_SAMPLE_RATE shr 24 and 0xff).toByte()

    header[28] = (BYTE_RATE and 0xff).toByte() // Byte rate = (Sample Rate * BitsPerSample * Channels) / 8
    header[29] = (BYTE_RATE shr 8 and 0xff).toByte()
    header[30] = (BYTE_RATE shr 16 and 0xff).toByte()
    header[31] = (BYTE_RATE shr 24 and 0xff).toByte()

    header[32] = (NUMBER_CHANNELS * BITS_PER_SAMPLE / 8).toByte() //  16 Bits stereo
    header[33] = 0

    header[34] = BITS_PER_SAMPLE.toByte() // Bits per sample
    header[35] = 0

    header[36] = 'd'.toByte()
    header[37] = 'a'.toByte()
    header[38] = 't'.toByte()
    header[39] = 'a'.toByte()

    header[40] = (0 and 0xff).toByte() // Size of the data section.
    header[41] = (0 shr 8 and 0xff).toByte()
    header[42] = (0 shr 16 and 0xff).toByte()
    header[43] = (0 shr 24 and 0xff).toByte()

    return header
}

fun updateHeaderInformation(data: ArrayList<Byte>) {
    val fileSize = data.size
    val contentSize = fileSize - 44

    data[4] = (fileSize and 0xff).toByte() // Size of the overall file
    data[5] = (fileSize shr 8 and 0xff).toByte()
    data[6] = (fileSize shr 16 and 0xff).toByte()
    data[7] = (fileSize shr 24 and 0xff).toByte()

    data[40] = (contentSize and 0xff).toByte() // Size of the data section.
    data[41] = (contentSize shr 8 and 0xff).toByte()
    data[42] = (contentSize shr 16 and 0xff).toByte()
    data[43] = (contentSize shr 24 and 0xff).toByte()
}

const val RECORDER_SAMPLE_RATE = 16000
const val BITS_PER_SAMPLE: Short = 16
const val NUMBER_CHANNELS: Short = 1
const val BYTE_RATE = RECORDER_SAMPLE_RATE * NUMBER_CHANNELS * 16 / 8
