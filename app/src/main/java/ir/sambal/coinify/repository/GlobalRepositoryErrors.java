package ir.sambal.coinify.repository;

import android.content.Context;
import android.widget.Toast;

import ir.sambal.coinify.R;

public class GlobalRepositoryErrors {
    public static final int UNKNOWN = 0;
    public static final int SERVER_ERROR = 5;
    public static final int CONNECTION_ERROR = 2;

    public static boolean handleGlobalError(Context context, int errorNumber) {
        String message = null;
        switch (errorNumber) {
            case GlobalRepositoryErrors.CONNECTION_ERROR:
                message = context.getResources().getString(R.string.connection_error_please_retry);
                break;
            case GlobalRepositoryErrors.SERVER_ERROR:
                message = context.getResources().getString(R.string.server_error_please_retry);
                break;
            case GlobalRepositoryErrors.UNKNOWN:
                message = context.getResources().getString(R.string.unknown_error_contact_support);
                break;
        }
        if (message != null) {
            int duration = Toast.LENGTH_LONG;

            Toast toast = Toast.makeText(context, message, duration);
            toast.show();
            return true;
        }
        return false;
    }
}
