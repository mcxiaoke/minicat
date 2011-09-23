/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: D:\\apps\\android\\fanfouhd\\src\\com\\fanfou\\app\\service\\IFanFouServiceCallback.aidl
 */
package com.fanfou.app.service;
public interface IFanFouServiceCallback extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.fanfou.app.service.IFanFouServiceCallback
{
private static final java.lang.String DESCRIPTOR = "com.fanfou.app.service.IFanFouServiceCallback";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.fanfou.app.service.IFanFouServiceCallback interface,
 * generating a proxy if needed.
 */
public static com.fanfou.app.service.IFanFouServiceCallback asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = (android.os.IInterface)obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.fanfou.app.service.IFanFouServiceCallback))) {
return ((com.fanfou.app.service.IFanFouServiceCallback)iin);
}
return new com.fanfou.app.service.IFanFouServiceCallback.Stub.Proxy(obj);
}
public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_onHomeReceived:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.onHomeReceived(_arg0);
return true;
}
case TRANSACTION_onMentionsReceived:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.onMentionsReceived(_arg0);
return true;
}
case TRANSACTION_onMessageReceived:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.onMessageReceived(_arg0);
return true;
}
case TRANSACTION_onActionReceived:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
this.onActionReceived(_arg0, _arg1);
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.fanfou.app.service.IFanFouServiceCallback
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
public void onHomeReceived(int count) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(count);
mRemote.transact(Stub.TRANSACTION_onHomeReceived, _data, null, android.os.IBinder.FLAG_ONEWAY);
}
finally {
_data.recycle();
}
}
public void onMentionsReceived(int count) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(count);
mRemote.transact(Stub.TRANSACTION_onMentionsReceived, _data, null, android.os.IBinder.FLAG_ONEWAY);
}
finally {
_data.recycle();
}
}
public void onMessageReceived(int count) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(count);
mRemote.transact(Stub.TRANSACTION_onMessageReceived, _data, null, android.os.IBinder.FLAG_ONEWAY);
}
finally {
_data.recycle();
}
}
public void onActionReceived(int type, int count) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(type);
_data.writeInt(count);
mRemote.transact(Stub.TRANSACTION_onActionReceived, _data, null, android.os.IBinder.FLAG_ONEWAY);
}
finally {
_data.recycle();
}
}
}
static final int TRANSACTION_onHomeReceived = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_onMentionsReceived = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_onMessageReceived = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_onActionReceived = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
}
public void onHomeReceived(int count) throws android.os.RemoteException;
public void onMentionsReceived(int count) throws android.os.RemoteException;
public void onMessageReceived(int count) throws android.os.RemoteException;
public void onActionReceived(int type, int count) throws android.os.RemoteException;
}
