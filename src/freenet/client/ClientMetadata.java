package freenet.client;

/**
 * Stores the metadata that the client might actually be interested in.
 */
public class ClientMetadata {
	
	/** The document MIME type */
	private String mimeType;

	public ClientMetadata(String mime) {
		mimeType = mime;
	}

	/** Create an empty ClientMetadata instance */
	ClientMetadata() {
		mimeType = null;
	}
	
	/** Get the document MIME type. Will always be a valid MIME type, unless there
	 * has been an error; if it is unknown, will return application/octet-stream. */
	public String getMIMEType() {
		if(mimeType == null || mimeType.length() == 0)
			return DefaultMIMETypes.DEFAULT_MIME_TYPE;
		return mimeType;
	}

	/**
	 * Merge the given ClientMetadata, without overwriting our
	 * existing information.
	 */
	public void mergeNoOverwrite(ClientMetadata clientMetadata) {
		if(mimeType == null || mimeType.equals(""))
			mimeType = clientMetadata.mimeType;
	}

	public boolean isTrivial() {
		return (mimeType == null || mimeType.equals(""));
	}
}
