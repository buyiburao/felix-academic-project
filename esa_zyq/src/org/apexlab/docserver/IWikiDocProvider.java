package org.apexlab.docserver;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IWikiDocProvider extends Remote{
	public String getDocContentByTitle(String title) throws RemoteException;

}
