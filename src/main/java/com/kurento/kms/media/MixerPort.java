package com.kurento.kms.media;

import java.io.IOException;

import org.apache.thrift.TException;
import org.apache.thrift.async.AsyncMethodCallback;

import com.kurento.kms.api.MediaObjectNotFoundException;
import com.kurento.kms.api.MediaServerException;
import com.kurento.kms.api.MediaServerService;
import com.kurento.kms.api.MediaServerService.AsyncClient.getMixer_call;
import com.kurento.kms.media.internal.MediaServerServiceManager;

public class MixerPort extends Joinable {

	private static final long serialVersionUID = 1L;

	public MixerPort(com.kurento.kms.api.MediaObject mixerPort) {
		super(mixerPort);
	}

	public void getMixer(final Continuation<Mixer> cont) throws IOException {
		try {
			MediaServerServiceManager manager = MediaServerServiceManager
					.getInstance();
			MediaServerService.AsyncClient service = manager
					.getMediaServerServiceAsync();
			service.getMixer(
					mediaObject,
					new AsyncMethodCallback<MediaServerService.AsyncClient.getMixer_call>() {
						@Override
						public void onComplete(getMixer_call response) {
							try {
								com.kurento.kms.api.MediaObject mixer = response
										.getResult();
								cont.onSuccess(new Mixer(mixer));
							} catch (MediaObjectNotFoundException e) {
								cont.onError(new RuntimeException(e
										.getMessage(), e));
							} catch (MediaServerException e) {
								cont.onError(new RuntimeException(e
										.getMessage(), e));
							} catch (TException e) {
								cont.onError(new IOException(e.getMessage(), e));
							}
						}

						@Override
						public void onError(Exception exception) {
							cont.onError(exception);
						}
					});
			manager.releaseMediaServerServiceAsync(service);
		} catch (TException e) {
			throw new IOException(e.getMessage(), e);
		}
	}

}
