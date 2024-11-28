package de.ehealth.evek.network.interfaces;

import java.io.IOException;

import de.ehealth.evek.entity.User;

interface IComSender {
	
	void sendPCUser(User pcUser) throws IOException;


}
