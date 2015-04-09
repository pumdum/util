package eu.anasta.bluemind.smileyms;

import net.bluemind.lmtp.backend.ILmtpFilterFactory;
import net.bluemind.lmtp.backend.IMessageFilter;

public class LmtpFilterFactory implements ILmtpFilterFactory {

	public LmtpFilterFactory() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public int getPriority() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public IMessageFilter getEngine() {
		// TODO Auto-generated method stub
		return new ImipSmileyMsFilter();
	}

}
