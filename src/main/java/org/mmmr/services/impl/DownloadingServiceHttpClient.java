package org.mmmr.services.impl;

import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.impl.client.ContentEncodingHttpClient;
import org.mmmr.services.ExceptionAndLogHandler;
import org.mmmr.services.Messages;

/**
 * {@link HttpClient} (httpcomponents from apache) download service<br>
 * download and sourcecode available from ... (see links)
 * 
 * @author Jurgen
 * 
 * @see http://hc.apache.org/
 */
public class DownloadingServiceHttpClient extends DownloadingServiceSimple {
    public class DownloadProgressMonitor extends ProgressMonitor implements PropertyChangeListener {
        private final SwingWorker<String, Void> sw;

        public DownloadProgressMonitor(final Component parentComponent, final Object message, final String note, final OutputStream target,
                final HttpEntity entity) {
            super(parentComponent, message, note, 0, 100);

            this.sw = new SwingWorker<String, Void>() {
                @Override
                public String doInBackground() {
                    this.setProgress(0);

                    try {
                        byte[] buffer = new byte[1024 * 8 * 4];
                        long dl = 0;
                        InputStream uin = entity.getContent();
                        int read;
                        while ((read = uin.read(buffer)) != -1) {
                            target.write(buffer, 0, read);
                            dl += read;
                            long contentLength = entity.getContentLength();
                            int percentage = Math.min(100, Math.max(0, (int) ((dl * 100l) / contentLength)));
                            this.setProgress(percentage);
                        }
                        target.close();
                        uin.close();
                    } catch (Exception ex) {
                        return String.valueOf(ex);
                    }
                    return note;
                }

                @Override
                public void done() {
                    DownloadProgressMonitor.this.close();
                    // System.out.println("done");
                }
            };

            this.sw.addPropertyChangeListener(this);
            this.sw.execute();
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if ("progress" == evt.getPropertyName()) { //$NON-NLS-1$
                int progress = (Integer) evt.getNewValue();
                this.setProgress(progress);
                String message = String.format(Messages.getString("DownloadingServiceHttpClient.completed") + " %d%%.", progress); //$NON-NLS-1$ //$NON-NLS-2$
                this.setNote(message);
                // System.out.println(message);
                if (this.isCanceled() || this.sw.isDone()) {
                    if (this.isCanceled()) {
                        this.sw.cancel(true);
                        // System.out.println("Task canceled.");
                    } else {
                        // System.out.println("Task completed.");
                    }
                }
            }
        }
    }

    /**
     * 
     * @see org.mmmr.services.interfaces.DownloadingServiceI#downloadURL(java.net.URL)
     */
    @Override
    public byte[] downloadURL(URL url) throws IOException {
        ByteArrayOutputStream target = new ByteArrayOutputStream();
        this.downloadURL(url, target);
        return target.toByteArray();
    }

    /**
     * 
     * @see org.mmmr.services.interfaces.DownloadingServiceI#downloadURL(java.net.URL, java.io.File)
     */
    @Override
    public void downloadURL(URL url, File target) throws IOException {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(target);
            this.downloadURL(url, fos);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (Exception ex) {
                    //
                }
            }
        }
    }

    /**
     * 
     * @see org.mmmr.services.interfaces.DownloadingServiceI#downloadURL(java.net.URL, java.io.OutputStream)
     */
    @Override
    public void downloadURL(URL url, OutputStream target) throws IOException {
        ExceptionAndLogHandler.log(url);
        ContentEncodingHttpClient httpclient = new ContentEncodingHttpClient();
        HttpGet request;
        try {
            request = new HttpGet(url.toURI());
        } catch (URISyntaxException ex) {
            throw new IOException(ex);
        }
        HttpResponse response = httpclient.execute(request);
        if (response.getStatusLine().getStatusCode() == 404) {
            throw new IOException(url + ": 404");//$NON-NLS-1$
        }
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            try {
                new DownloadProgressMonitor(null, Messages.getString("DownloadingServiceHttpClient.downloading"), String.valueOf(url), target, entity).sw.get(); //$NON-NLS-1$
                return;
            } catch (InterruptedException ex) {
                //
            } catch (ExecutionException ex) {
                throw new RuntimeException(ex);
            }
        }
        throw new IOException("" + url); //$NON-NLS-1$
    }

    /**
     * 
     * @see org.mmmr.services.interfaces.DownloadingServiceI#trace(java.net.URL)
     */
    @Override
    public String trace(URL url) throws IOException {
        ExceptionAndLogHandler.log(url);
        ContentEncodingHttpClient httpclient = new ContentEncodingHttpClient();
        httpclient.getParams().setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, false);
        HttpHead request;
        try {
            request = new HttpHead(url.toURI());
        } catch (URISyntaxException ex) {
            throw new IOException(ex);
        }
        HttpResponse response = httpclient.execute(request);
        if (response.getStatusLine().getStatusCode() == 404) {
            throw new IOException(url + ": 404");//$NON-NLS-1$
        }

        Header[] headers = response.getHeaders("Location");//$NON-NLS-1$
        if ((headers == null) || (headers.length == 0)) {
            return url.toExternalForm();
        }
        return headers[0].getValue();
    }
}
