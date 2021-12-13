package sb.yoon.kiosk.libs

import android.content.Context
import android.content.Intent
import android.hardware.usb.UsbConstants
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.getSystemService
import sb.yoon.kiosk.model.CartMenu
import sb.yoon.kiosk.noPaperDlg
import java.lang.Exception
import java.lang.StringBuilder
import java.nio.charset.Charset
import java.util.HashMap
import java.util.regex.Pattern

//Printer ------------------------------------------
public const val TAG = "UsbMenuActivity"

// Intent
public const val ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION"

// hwasung vendor id string
const val HWASUNG_VENDOR_ID = "6"
const val MAX_IMAGE_LINE = 24 // NOTE! do not edit!

// printer commands
const val CMD_DLE: Byte = 0x10
const val CMD_ESC: Byte = 0x1b
const val CMD_GS: Byte = 0x1d
const val CMD_FS: Byte = 0x1c

fun printInent(i: Intent?) {
    try {
        //Log.e("KTC","-------------------------------------------------------");
        //util.saveLog("-------------------------------------------------------");
        if (i != null) {
            val extras = i.extras
            if (extras != null) {
                val keys: Set<*> = extras.keySet()
                for (_key in extras.keySet()) {
                    Log.e("KTC", "key=" + _key + " : " + extras[_key])
                }
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
    }
}

fun readStatus(context: Context, mConnection: UsbDeviceConnection?, mDevice: UsbDevice?) {
    val mIntBuf: IntArray
    val mIntBuf3 = IntArray(12)
    val mDataCnt: Int
    val deviceCnt: Int = setDevice(context).first
    if (deviceCnt != 0) {
        mDataCnt = 6
        mIntBuf = IntArray(mDataCnt)
        mIntBuf[0] = 0x10
        mIntBuf[1] = 0xAA
        mIntBuf[2] = 0x55
        mIntBuf[3] = 0x80
        mIntBuf[4] = 0x54
        mIntBuf[5] = 0xAB
        sendCommand(mConnection, mDevice, mIntBuf, mDataCnt)
        receiveCommand(mConnection, mDevice, mIntBuf3)

        // display status value of printer
        Log.d(TAG, "status : " + mIntBuf3[0])
        if (mIntBuf3[0] == 0x09) {
            val noPaperDlg = noPaperDlg(context)
            noPaperDlg.callFunction()
        }
    }
}

//디바이스 설정
fun setDevice(context: Context): Pair<Int, UsbDeviceConnection?> {
    var device_cnt = 0 // hwasung device detect counter
    val mUsbManager = getSystemService(context, UsbManager::class.java)
    val deviceList = mUsbManager!!.deviceList
    val deviceIterator: Iterator<UsbDevice> = deviceList.values.iterator()
    var mDevice: UsbDevice? = null
    var mConnection: UsbDeviceConnection? = null

    while (deviceIterator.hasNext()) {
        val device = deviceIterator.next()
        val mVendorId = device.vendorId.toString()
        val b = Pattern.matches(HWASUNG_VENDOR_ID, mVendorId)
        if (b) {
            mDevice = device
            device_cnt++
            break
        } else {
            device_cnt = 0
        }
    }

    if (device_cnt == 0) {
        return Pair(0, mConnection)
    }

    // 첫번째 기장치 인터페이스 불러오
    val intf = mDevice!!.getInterface(0)

    // endpoint type BULK
    val ep = intf.getEndpoint(0)
    if (ep.type != UsbConstants.USB_ENDPOINT_XFER_BULK) {
        Log.e(TAG, "endpoint is not BULK type")
        return Pair(0, mConnection)
    }

    val connection = mUsbManager.openDevice(mDevice)
    if (connection != null && connection.claimInterface(intf, true)) {
        mConnection = connection // USB OPEN SUCCESS
    }
    return Pair(device_cnt, mConnection)
}


//커맨드 보내기
public fun sendCommand(
    mConnection: UsbDeviceConnection?,
    device: UsbDevice?,
    mIntBuf: IntArray,
    mDataCnt: Int
) {
    val mRet: Int
    val intf = device!!.getInterface(0)
    val ep = intf.getEndpoint(0)
    var mByteBuf = ByteArray(mDataCnt)
    var i: Int

    // convert integer to byte
    mByteBuf = ByteArray(mDataCnt)
    i = 0
    while (i < mDataCnt) {
        mByteBuf[i] = mIntBuf[i].toByte()
        i++
    }
    mRet = mConnection!!.bulkTransfer(ep, mByteBuf, mDataCnt, 5000)
    if (mRet == -1) Log.d("send command fail", "" + mRet)
}

//커맨드 받았을 때
public fun receiveCommand(
    mConnection: UsbDeviceConnection?,
    device: UsbDevice?,
    mIntBuf: IntArray
) {
    val mRet: Int
    val intf = device!!.getInterface(0)
    // receive BULK-IN endpoint6(mAddress:0x86)
    val ep = intf.getEndpoint(2) //엡손 호환 테스트용 수정됨
    val mByteBuf = ByteArray(1)

    // 1byte read, time out 1second
    mRet = mConnection!!.bulkTransfer(ep, mByteBuf, 1, 5000)
    if (mRet == -1) {
        Log.d("status fail", "수신 실패")
        Log.d("receive command ", "" + mRet)
    }
    // set 1byte status data to integer buffer and return
    mIntBuf[0] = mByteBuf[0].toInt()
}


//프린트 내용-------------------------
@RequiresApi(api = Build.VERSION_CODES.N)
fun onClick_usb_text2(
    context: Context,
    mConnection: UsbDeviceConnection,
    mDevice: UsbDevice,
    cartMenuList: MutableList<CartMenu>,
    orderNumber: Int,
    totalPrice: Int,
    card_name: String,
    acc_name: String,
    card_num: String,
    card_approval_num: String,
    card_approval_date: String
) {
    val mPrice: String
    var mTotalPrice: String
    val mEndMsg: String
    var mStr: String
    var mIntBuf: IntArray
    var mDataCnt: Int
    var i: Int
    var mByteBuf: ByteArray
    val orderNum: String = """
             주문번호 : ${Integer.toString(orderNumber)}
             
             """.trimIndent()
    val mCafeName: String = "\n11호관 커피숍\n"
    val mCafeInfo: String = """
             울산시 남구 대학로 93 11호관 305호
             TEL.052-220-5757
             
             """.trimIndent()
    val mBar: String = " ------------------------ \n"
    val mPriceInfo: String = "   메뉴\t\t\t\t수량\t    가격\n"
    val quantityTable = HashMap<String, Int>()
    val priceTable = HashMap<String, Int>()
    for (cartMenu in cartMenuList) {
        val name = cartMenu.name
        quantityTable[name] = quantityTable.getOrDefault(name, 0) + 1
        priceTable[name] = priceTable.getOrDefault(name, 0) + cartMenu.totalPrice
    }
    val mStrBodyBuilder: StringBuilder = StringBuilder()
    for (name in quantityTable.keys) {
        if (name.length > 6) {
            mStrBodyBuilder.append(
                String.format(
                    "  %s\t\t%d\t    \\%d\n",
                    name,
                    quantityTable[name],
                    priceTable[name]
                )
            )
        } else {
            mStrBodyBuilder.append(
                String.format(
                    "  %s\t\t\t%d\t    \\%d\n",
                    name,
                    quantityTable[name],
                    priceTable[name]
                )
            )
        }
    }
    mPrice = mStrBodyBuilder.toString()

    // set Hwasung Syatem usb device
    val deviceCnt = setDevice(context)
    if (deviceCnt.first != 0) {
        // 주문번호 부분
        //centering print position
        mIntBuf = IntArray(3)
        mIntBuf[0] = CMD_ESC.toInt()
        mIntBuf[1] = 0x61
        mIntBuf[2] = 0x01
        mDataCnt = 3
        // call send data
        sendCommand(mConnection, mDevice, mIntBuf, mDataCnt)

        // 가로세로 2배 사이즈 조절
        mIntBuf = IntArray(8)
        mIntBuf[0] = CMD_GS.toInt()
        mIntBuf[1] = '!'.code
        mIntBuf[2] = 0x11
        mDataCnt = 3

        // 데이터 전송 호출
        sendCommand(mConnection, mDevice, mIntBuf, mDataCnt)

        //주문번호 전송
        mByteBuf = orderNum.toByteArray(Charset.forName("EUC-KR"))
        mDataCnt = mByteBuf.size
        mIntBuf = IntArray(mDataCnt)
        // 정수 버퍼에 데이터 설정
        i = 0
        while (i < mDataCnt) {
            mIntBuf[i] = mByteBuf[i].toInt()
            i++
        }
        // 데이터 전송 호출
        sendCommand(mConnection, mDevice, mIntBuf, mDataCnt)

        // 세로 2배사이즈로 변경
        mIntBuf[0] = CMD_GS.toInt()
        mIntBuf[1] = '!'.code
        mIntBuf[2] = 0x01
        mDataCnt = 3

        // 데이터 전송 호출
        sendCommand(mConnection, mDevice, mIntBuf, mDataCnt)

        //Todo mCafeName 부분
        //mCafeName 호출
        mByteBuf = mCafeName.toByteArray(Charset.forName("EUC-KR"))
        mDataCnt = mByteBuf.size
        mIntBuf = IntArray(mDataCnt)

        // set data to integer buffer
        i = 0
        while (i < mDataCnt) {
            mIntBuf[i] = mByteBuf[i].toInt()
            i++
        }
        // call send data
        sendCommand(mConnection, mDevice, mIntBuf, mDataCnt)

        //2배 사이즈 끝
        mIntBuf[0] = CMD_GS.toInt()
        mIntBuf[1] = '!'.code
        mIntBuf[2] = 0x00
        mDataCnt = 3

        // 데이터 전송 호출
        sendCommand(mConnection, mDevice, mIntBuf, mDataCnt)

        //Todo mCafeInfo 부분
        //mCafeInfo 호출
        mByteBuf = mCafeInfo.toByteArray(Charset.forName("EUC-KR"))
        mDataCnt = mByteBuf.size
        mIntBuf = IntArray(mDataCnt)
        i = 0
        while (i < mDataCnt) {
            mIntBuf[i] = mByteBuf[i].toInt()
            i++
        }
        sendCommand(mConnection, mDevice, mIntBuf, mDataCnt)

        // 중앙정렬 끝
        mIntBuf[0] = CMD_ESC.toInt()
        mIntBuf[1] = 0x61
        mIntBuf[2] = 0x00
        mDataCnt = 3
        sendCommand(mConnection, mDevice, mIntBuf, mDataCnt)

        //Todo mBar부분
        //2배 사이즈 끝
        mIntBuf[0] = CMD_GS.toInt()
        mIntBuf[1] = '!'.code
        mIntBuf[2] = 0x11
        mDataCnt = 3

        // 데이터 전송 호출
        sendCommand(mConnection, mDevice, mIntBuf, mDataCnt)

        //call mBar
        mByteBuf = mBar.toByteArray(Charset.forName("EUC-KR"))
        mDataCnt = mByteBuf.size
        mIntBuf = IntArray(mDataCnt)

        // set data to integer buffer
        i = 0
        while (i < mDataCnt) {
            mIntBuf[i] = mByteBuf[i].toInt()
            i++
        }
        // call send data
        sendCommand(mConnection, mDevice, mIntBuf, mDataCnt)

        //2배 사이즈 끝
        mIntBuf[0] = CMD_GS.toInt()
        mIntBuf[1] = '!'.code
        mIntBuf[2] = 0x00
        mDataCnt = 3

        // 데이터 전송 호출
        sendCommand(mConnection, mDevice, mIntBuf, mDataCnt)
        //Todo mPriceInfo 부분
        mByteBuf = mPriceInfo.toByteArray(Charset.forName("EUC-KR"))
        mDataCnt = mByteBuf.size
        mIntBuf = IntArray(mDataCnt)
        i = 0
        while (i < mDataCnt) {
            mIntBuf[i] = mByteBuf[i].toInt()
            i++
        }
        sendCommand(mConnection, mDevice, mIntBuf, mDataCnt)
        //Todo mBar 부분

        // 가로 2배
        mIntBuf[0] = CMD_GS.toInt()
        mIntBuf[1] = '!'.code
        mIntBuf[2] = 0x11
        mDataCnt = 3
        // call send data
        sendCommand(mConnection, mDevice, mIntBuf, mDataCnt)

        // mBar 출력
        mByteBuf = mBar.toByteArray(Charset.forName("EUC-KR"))
        mDataCnt = mByteBuf.size
        mIntBuf = IntArray(mDataCnt)
        // set data to integer buffer
        i = 0
        while (i < mDataCnt) {
            mIntBuf[i] = mByteBuf[i].toInt()
            i++
        }
        // call send data
        sendCommand(mConnection, mDevice, mIntBuf, mDataCnt)
        mIntBuf[0] = CMD_GS.toInt()
        mIntBuf[1] = '!'.code
        mIntBuf[2] = 0x00
        mDataCnt = 3
        // call send data
        sendCommand(mConnection, mDevice, mIntBuf, mDataCnt)

        // Todo mPrice 부분
        // Bold On
        mIntBuf[0] = CMD_ESC.toInt()
        mIntBuf[1] = 0x45
        mIntBuf[2] = 0x01
        mDataCnt = 3
        sendCommand(mConnection, mDevice, mIntBuf, mDataCnt)
        mByteBuf = mPrice.toByteArray(Charset.forName("EUC-KR"))
        mDataCnt = mByteBuf.size
        mIntBuf = IntArray(mDataCnt)
        // set data to integer buffer
        i = 0
        while (i < mDataCnt) {
            mIntBuf[i] = mByteBuf[i].toInt()
            i++
        }
        // call send data
        sendCommand(mConnection, mDevice, mIntBuf, mDataCnt)

        //Bold Off
        mIntBuf[0] = CMD_ESC.toInt()
        mIntBuf[1] = 0x45
        mIntBuf[2] = 0x00
        mDataCnt = 3
        sendCommand(mConnection, mDevice, mIntBuf, mDataCnt)

        // Todo mBar
        mIntBuf[0] = CMD_GS.toInt()
        mIntBuf[1] = '!'.code
        mIntBuf[2] = 0x10
        mDataCnt = 3
        // call send data
        sendCommand(mConnection, mDevice, mIntBuf, mDataCnt)

        // mBar 출력
        mByteBuf = mBar.toByteArray(Charset.forName("EUC-KR"))
        mDataCnt = mByteBuf.size
        mIntBuf = IntArray(mDataCnt)
        // set data to integer buffer
        i = 0
        while (i < mDataCnt) {
            mIntBuf[i] = mByteBuf[i].toInt()
            i++
        }
        // call send data
        sendCommand(mConnection, mDevice, mIntBuf, mDataCnt)


        // 소비세
        val p: Int = Math.toIntExact(Math.round(totalPrice / 1.1))
        mIntBuf[0] = CMD_ESC.toInt()
        mIntBuf[1] = 0x61
        mIntBuf[2] = 0x02
        mDataCnt = 3
        sendCommand(mConnection, mDevice, mIntBuf, mDataCnt)
        mIntBuf[0] = CMD_GS.toInt()
        mIntBuf[1] = '!'.code
        mIntBuf[2] = 0x11
        mDataCnt = 3
        sendCommand(mConnection, mDevice, mIntBuf, mDataCnt)
        mTotalPrice = "  합계금액 : "
        mTotalPrice += "$totalPrice 원 \n"
        mByteBuf = mTotalPrice.toByteArray(Charset.forName("EUC-KR"))
        mDataCnt = mByteBuf.size
        mIntBuf = IntArray(mDataCnt)
        // set data to integer buffer
        i = 0
        while (i < mDataCnt) {
            mIntBuf[i] = mByteBuf[i].toInt()
            i++
        }
        // call send data
        sendCommand(mConnection, mDevice, mIntBuf, mDataCnt)
        mTotalPrice = "  공급가액 : "
        mTotalPrice += "$p 원 \n"
        mByteBuf = mTotalPrice.toByteArray(Charset.forName("EUC-KR"))
        mDataCnt = mByteBuf.size
        mIntBuf = IntArray(mDataCnt)
        // set data to integer buffer
        i = 0
        while (i < mDataCnt) {
            mIntBuf[i] = mByteBuf[i].toInt()
            i++
        }
        // call send data
        sendCommand(mConnection, mDevice, mIntBuf, mDataCnt)
        mTotalPrice = "   부가세 : "
        mTotalPrice += "${totalPrice - p} 원 \n"
        mByteBuf = mTotalPrice.toByteArray(Charset.forName("EUC-KR"))
        mDataCnt = mByteBuf.size
        mIntBuf = IntArray(mDataCnt)
        // set data to integer buffer
        i = 0
        while (i < mDataCnt) {
            mIntBuf[i] = mByteBuf[i].toInt()
            i++
        }
        // call send data
        sendCommand(mConnection, mDevice, mIntBuf, mDataCnt)


        // Size width 2x
        mIntBuf[0] = CMD_GS.toInt()
        mIntBuf[1] = '!'.code
        mIntBuf[2] = 0x10
        mDataCnt = 3
        // call send data
        sendCommand(mConnection, mDevice, mIntBuf, mDataCnt)
        mIntBuf[0] = CMD_ESC.toInt()
        mIntBuf[1] = 0x61
        mIntBuf[2] = 0x00
        mDataCnt = 3
        sendCommand(mConnection, mDevice, mIntBuf, mDataCnt)


        // Todo mBar
        mByteBuf = mBar.toByteArray(Charset.forName("EUC-KR"))
        mDataCnt = mByteBuf.size
        mIntBuf = IntArray(mDataCnt)
        // set data to integer buffer
        i = 0
        while (i < mDataCnt) {
            mIntBuf[i] = mByteBuf[i].toInt()
            i++
        }
        // call send data
        sendCommand(mConnection, mDevice, mIntBuf, mDataCnt)

        // Todo 사업자 정보
        //centering print position
        mIntBuf[0] = CMD_ESC.toInt()
        mIntBuf[1] = 0x61
        mIntBuf[2] = 0x01
        mDataCnt = 3
        // call send data
        sendCommand(mConnection, mDevice, mIntBuf, mDataCnt)

        // Size height 2x
        mIntBuf[0] = CMD_GS.toInt()
        mIntBuf[1] = '!'.code
        mIntBuf[2] = 0x01
        mDataCnt = 3
        // call send data
        sendCommand(mConnection, mDevice, mIntBuf, mDataCnt)
        mStr = "\n신용카드 승인 정보\n"
        mByteBuf = mStr.toByteArray(Charset.forName("EUC-KR"))
        mDataCnt = mByteBuf.size
        mIntBuf = IntArray(mDataCnt)
        // set data to integer buffer
        i = 0
        while (i < mDataCnt) {
            mIntBuf[i] = mByteBuf[i].toInt()
            i++
        }
        // call send data
        sendCommand(mConnection, mDevice, mIntBuf, mDataCnt)

        // Size 1x
        mIntBuf[0] = CMD_GS.toInt()
        mIntBuf[1] = '!'.code
        mIntBuf[2] = 0x00
        mDataCnt = 3
        // call send data
        sendCommand(mConnection, mDevice, mIntBuf, mDataCnt)

        // Bold On
        mIntBuf[0] = CMD_ESC.toInt()
        mIntBuf[1] = 0x45
        mIntBuf[2] = 0x01
        mDataCnt = 3
        sendCommand(mConnection, mDevice, mIntBuf, mDataCnt)
        mStr = "카드명: "
        mByteBuf = mStr.toByteArray(Charset.forName("EUC-KR"))
        mDataCnt = mByteBuf.size
        mIntBuf = IntArray(mDataCnt)
        // set data to integer buffer
        i = 0
        while (i < mDataCnt) {
            mIntBuf[i] = mByteBuf[i].toInt()
            i++
        }
        // call send data
        sendCommand(mConnection, mDevice, mIntBuf, mDataCnt)

        // Bold Off
        mIntBuf[0] = CMD_ESC.toInt()
        mIntBuf[1] = 0x45
        mIntBuf[2] = 0x00
        mDataCnt = 3
        sendCommand(mConnection, mDevice, mIntBuf, mDataCnt)
        mStr = """
                $card_name
                
                """.trimIndent()
        mByteBuf = mStr.toByteArray(Charset.forName("EUC-KR"))
        mDataCnt = mByteBuf.size
        mIntBuf = IntArray(mDataCnt)
        // set data to integer buffer
        i = 0
        while (i < mDataCnt) {
            mIntBuf[i] = mByteBuf[i].toInt()
            i++
        }
        // call send data
        sendCommand(mConnection, mDevice, mIntBuf, mDataCnt)
        mIntBuf[0] = CMD_ESC.toInt()
        mIntBuf[1] = 0x45
        mIntBuf[2] = 0x01
        mDataCnt = 3
        sendCommand(mConnection, mDevice, mIntBuf, mDataCnt)
        mStr = "카드번호 : "
        mByteBuf = mStr.toByteArray(Charset.forName("EUC-KR"))
        mDataCnt = mByteBuf.size
        mIntBuf = IntArray(mDataCnt)
        // set data to integer buffer
        i = 0
        while (i < mDataCnt) {
            mIntBuf[i] = mByteBuf[i].toInt()
            i++
        }
        // call send data
        sendCommand(mConnection, mDevice, mIntBuf, mDataCnt)

        // Bold Off
        mIntBuf[0] = CMD_ESC.toInt()
        mIntBuf[1] = 0x45
        mIntBuf[2] = 0x00
        mDataCnt = 3
        sendCommand(mConnection, mDevice, mIntBuf, mDataCnt)
        mStr = "$card_num**********\n"
        mByteBuf = mStr.toByteArray(Charset.forName("EUC-KR"))
        mDataCnt = mByteBuf.size
        mIntBuf = IntArray(mDataCnt)
        // set data to integer buffer
        i = 0
        while (i < mDataCnt) {
            mIntBuf[i] = mByteBuf[i].toInt()
            i++
        }
        // call send data
        sendCommand(mConnection, mDevice, mIntBuf, mDataCnt)
        mIntBuf[0] = CMD_ESC.toInt()
        mIntBuf[1] = 0x45
        mIntBuf[2] = 0x01
        mDataCnt = 3
        sendCommand(mConnection, mDevice, mIntBuf, mDataCnt)
        mIntBuf[0] = CMD_ESC.toInt()
        mIntBuf[1] = 0x45
        mIntBuf[2] = 0x01
        mDataCnt = 3
        sendCommand(mConnection, mDevice, mIntBuf, mDataCnt)
        mStr = "매입사명 : "
        mByteBuf = mStr.toByteArray(Charset.forName("EUC-KR"))
        mDataCnt = mByteBuf.size
        mIntBuf = IntArray(mDataCnt)
        // set data to integer buffer
        i = 0
        while (i < mDataCnt) {
            mIntBuf[i] = mByteBuf[i].toInt()
            i++
        }
        // call send data
        sendCommand(mConnection, mDevice, mIntBuf, mDataCnt)

        // Bold Off
        mIntBuf[0] = CMD_ESC.toInt()
        mIntBuf[1] = 0x45
        mIntBuf[2] = 0x00
        mDataCnt = 3
        sendCommand(mConnection, mDevice, mIntBuf, mDataCnt)
        mStr = """
                $acc_name
                """.trimIndent()
        mByteBuf = mStr.toByteArray(Charset.forName("EUC-KR"))
        mDataCnt = mByteBuf.size
        mIntBuf = IntArray(mDataCnt)
        // set data to integer buffer
        i = 0
        while (i < mDataCnt) {
            mIntBuf[i] = mByteBuf[i].toInt()
            i++
        }
        // call send data
        sendCommand(mConnection, mDevice, mIntBuf, mDataCnt)
        mIntBuf[0] = CMD_ESC.toInt()
        mIntBuf[1] = 0x45
        mIntBuf[2] = 0x01
        mDataCnt = 3
        sendCommand(mConnection, mDevice, mIntBuf, mDataCnt)
        mStr = "\n사 업 자 : "
        mByteBuf = mStr.toByteArray(Charset.forName("EUC-KR"))
        mDataCnt = mByteBuf.size
        mIntBuf = IntArray(mDataCnt)
        // set data to integer buffer
        i = 0
        while (i < mDataCnt) {
            mIntBuf[i] = mByteBuf[i].toInt()
            i++
        }
        // call send data
        sendCommand(mConnection, mDevice, mIntBuf, mDataCnt)

        // Bold Off
        mIntBuf[0] = CMD_ESC.toInt()
        mIntBuf[1] = 0x45
        mIntBuf[2] = 0x00
        mDataCnt = 3
        sendCommand(mConnection, mDevice, mIntBuf, mDataCnt)
        mStr = "691-85-00176 엄문호\n"
        mByteBuf = mStr.toByteArray(Charset.forName("EUC-KR"))
        mDataCnt = mByteBuf.size
        mIntBuf = IntArray(mDataCnt)
        // set data to integer buffer
        i = 0
        while (i < mDataCnt) {
            mIntBuf[i] = mByteBuf[i].toInt()
            i++
        }
        // call send data
        sendCommand(mConnection, mDevice, mIntBuf, mDataCnt) // Bold On
        mIntBuf[0] = CMD_ESC.toInt()
        mIntBuf[1] = 0x45
        mIntBuf[2] = 0x01
        mDataCnt = 3
        sendCommand(mConnection, mDevice, mIntBuf, mDataCnt)
        mStr = "가맹점명 : "
        mByteBuf = mStr.toByteArray(Charset.forName("EUC-KR"))
        mDataCnt = mByteBuf.size
        mIntBuf = IntArray(mDataCnt)
        // set data to integer buffer
        i = 0
        while (i < mDataCnt) {
            mIntBuf[i] = mByteBuf[i].toInt()
            i++
        }
        // call send data
        sendCommand(mConnection, mDevice, mIntBuf, mDataCnt)

        // Bold Off
        mIntBuf[0] = CMD_ESC.toInt()
        mIntBuf[1] = 0x45
        mIntBuf[2] = 0x00
        mDataCnt = 3
        sendCommand(mConnection, mDevice, mIntBuf, mDataCnt)
        mStr = "11호관 커피숍\n"
        mByteBuf = mStr.toByteArray(Charset.forName("EUC-KR"))
        mDataCnt = mByteBuf.size
        mIntBuf = IntArray(mDataCnt)
        // set data to integer buffer
        i = 0
        while (i < mDataCnt) {
            mIntBuf[i] = mByteBuf[i].toInt()
            i++
        }
        // call send data
        sendCommand(mConnection, mDevice, mIntBuf, mDataCnt)
        mIntBuf[0] = CMD_ESC.toInt()
        mIntBuf[1] = 0x45
        mIntBuf[2] = 0x01
        mDataCnt = 3
        sendCommand(mConnection, mDevice, mIntBuf, mDataCnt)
        mStr = "가맹번호 : "
        mByteBuf = mStr.toByteArray(Charset.forName("EUC-KR"))
        mDataCnt = mByteBuf.size
        mIntBuf = IntArray(mDataCnt)
        // set data to integer buffer
        i = 0
        while (i < mDataCnt) {
            mIntBuf[i] = mByteBuf[i].toInt()
            i++
        }
        // call send data
        sendCommand(mConnection, mDevice, mIntBuf, mDataCnt)

        // Bold Off
        mIntBuf[0] = CMD_ESC.toInt()
        mIntBuf[1] = 0x45
        mIntBuf[2] = 0x00
        mDataCnt = 3
        sendCommand(mConnection, mDevice, mIntBuf, mDataCnt)
        mStr = "157431024\n"
        mByteBuf = mStr.toByteArray(Charset.forName("EUC-KR"))
        mDataCnt = mByteBuf.size
        mIntBuf = IntArray(mDataCnt)
        // set data to integer buffer
        i = 0
        while (i < mDataCnt) {
            mIntBuf[i] = mByteBuf[i].toInt()
            i++
        }
        // call send data
        sendCommand(mConnection, mDevice, mIntBuf, mDataCnt)
        mIntBuf[0] = CMD_ESC.toInt()
        mIntBuf[1] = 0x45
        mIntBuf[2] = 0x01
        mDataCnt = 3
        sendCommand(mConnection, mDevice, mIntBuf, mDataCnt)
        mStr = "승인일시 : "
        mByteBuf = mStr.toByteArray(Charset.forName("EUC-KR"))
        mDataCnt = mByteBuf.size
        mIntBuf = IntArray(mDataCnt)
        // set data to integer buffer
        i = 0
        while (i < mDataCnt) {
            mIntBuf[i] = mByteBuf[i].toInt()
            i++
        }
        // call send data
        sendCommand(mConnection, mDevice, mIntBuf, mDataCnt)

        // Bold Off
        mIntBuf[0] = CMD_ESC.toInt()
        mIntBuf[1] = 0x45
        mIntBuf[2] = 0x00
        mDataCnt = 3
        sendCommand(mConnection, mDevice, mIntBuf, mDataCnt)

        // Todo 승인일자 고치기
        mStr = """
                $card_approval_date
                
                """.trimIndent()
        mByteBuf = mStr.toByteArray(Charset.forName("EUC-KR"))
        mDataCnt = mByteBuf.size
        mIntBuf = IntArray(mDataCnt)
        // set data to integer buffer
        i = 0
        while (i < mDataCnt) {
            mIntBuf[i] = mByteBuf[i].toInt()
            i++
        }
        // call send data
        sendCommand(mConnection, mDevice, mIntBuf, mDataCnt)
        mIntBuf[0] = CMD_ESC.toInt()
        mIntBuf[1] = 0x45
        mIntBuf[2] = 0x01
        mDataCnt = 3
        sendCommand(mConnection, mDevice, mIntBuf, mDataCnt)
        mStr = "승인번호 : "
        mByteBuf = mStr.toByteArray(Charset.forName("EUC-KR"))
        mDataCnt = mByteBuf.size
        mIntBuf = IntArray(mDataCnt)
        // set data to integer buffer
        i = 0
        while (i < mDataCnt) {
            mIntBuf[i] = mByteBuf[i].toInt()
            i++
        }
        // call send data
        sendCommand(mConnection, mDevice, mIntBuf, mDataCnt)

        // Bold Off
        mIntBuf[0] = CMD_ESC.toInt()
        mIntBuf[1] = 0x45
        mIntBuf[2] = 0x00
        mDataCnt = 3
        sendCommand(mConnection, mDevice, mIntBuf, mDataCnt)
        mStr = """
                $card_approval_num
                
                """.trimIndent()
        mByteBuf = mStr.toByteArray(Charset.forName("EUC-KR"))
        mDataCnt = mByteBuf.size
        mIntBuf = IntArray(mDataCnt)
        // set data to integer buffer
        i = 0
        while (i < mDataCnt) {
            mIntBuf[i] = mByteBuf[i].toInt()
            i++
        }
        // call send data
        sendCommand(mConnection, mDevice, mIntBuf, mDataCnt)
        mIntBuf[0] = CMD_ESC.toInt()
        mIntBuf[1] = 0x45
        mIntBuf[2] = 0x01
        mDataCnt = 3
        sendCommand(mConnection, mDevice, mIntBuf, mDataCnt)
        mStr = "승인금 : "
        mByteBuf = mStr.toByteArray(Charset.forName("EUC-KR"))
        mDataCnt = mByteBuf.size
        mIntBuf = IntArray(mDataCnt)
        // set data to integer buffer
        i = 0
        while (i < mDataCnt) {
            mIntBuf[i] = mByteBuf[i].toInt()
            i++
        }
        // call send data
        sendCommand(mConnection, mDevice, mIntBuf, mDataCnt)

        // Bold Off
        mIntBuf[0] = CMD_ESC.toInt()
        mIntBuf[1] = 0x45
        mIntBuf[2] = 0x00
        mDataCnt = 3
        sendCommand(mConnection, mDevice, mIntBuf, mDataCnt)

        // 총금액 다시 생각해보기.
        mStr = """
                $totalPrice
                
                """.trimIndent()
        mByteBuf = mStr.toByteArray(Charset.forName("EUC-KR"))
        mDataCnt = mByteBuf.size
        mIntBuf = IntArray(mDataCnt)
        // set data to integer buffer
        i = 0
        while (i < mDataCnt) {
            mIntBuf[i] = mByteBuf[i].toInt()
            i++
        }
        // call send data
        sendCommand(mConnection, mDevice, mIntBuf, mDataCnt)

        // clear centering
        mIntBuf[0] = CMD_ESC.toInt()
        mIntBuf[1] = 0x61
        mIntBuf[2] = 0x00
        mDataCnt = 3
        sendCommand(mConnection, mDevice, mIntBuf, mDataCnt)

        // Size Width 2x
        mIntBuf[0] = CMD_GS.toInt()
        mIntBuf[1] = '!'.code
        mIntBuf[2] = 0x10
        mDataCnt = 3
        sendCommand(mConnection, mDevice, mIntBuf, mDataCnt)

        // Todo mBar
        mByteBuf = mBar.toByteArray(Charset.forName("EUC-KR"))
        mDataCnt = mByteBuf.size
        mIntBuf = IntArray(mDataCnt)
        // set data to integer buffer
        i = 0
        while (i < mDataCnt) {
            mIntBuf[i] = mByteBuf[i].toInt()
            i++
        }
        sendCommand(mConnection, mDevice, mIntBuf, mDataCnt)

        // Size Width 2x
        mIntBuf[0] = CMD_GS.toInt()
        mIntBuf[1] = '!'.code
        mIntBuf[2] = 0x00
        mDataCnt = 3
        sendCommand(mConnection, mDevice, mIntBuf, mDataCnt)

        // centering
        mIntBuf[0] = CMD_ESC.toInt()
        mIntBuf[1] = 0x61
        mIntBuf[2] = 0x01
        mDataCnt = 3
        sendCommand(mConnection, mDevice, mIntBuf, mDataCnt)

        // mEndMsg
        mEndMsg = "저희 11호관 커피를 이용해 주셔서 감사합니다!\n"
        mByteBuf = mEndMsg.toByteArray(Charset.forName("EUC-KR"))
        mDataCnt = mByteBuf.size
        mIntBuf = IntArray(mDataCnt)
        // set data to integer buffer
        i = 0
        while (i < mDataCnt) {
            mIntBuf[i] = mByteBuf[i].toInt()
            i++
        }
        sendCommand(mConnection, mDevice, mIntBuf, mDataCnt)

        // clear centering
        mIntBuf[0] = CMD_ESC.toInt()
        mIntBuf[1] = 0x61
        mIntBuf[2] = 0x00
        mDataCnt = 3
        sendCommand(mConnection, mDevice, mIntBuf, mDataCnt)

        // feeding
        mIntBuf[0] = 0x0a
        mIntBuf[1] = 0x0a
        mIntBuf[2] = 0x0a
        mIntBuf[3] = 0x0a
        mIntBuf[4] = 0x0a
        mDataCnt = 5
        sendCommand(mConnection, mDevice, mIntBuf, mDataCnt)

        // full cutting
        mIntBuf[0] = CMD_ESC.toInt()
        mIntBuf[1] = 0x69
        mDataCnt = 2
        sendCommand(mConnection, mDevice, mIntBuf, mDataCnt)
    }
}
