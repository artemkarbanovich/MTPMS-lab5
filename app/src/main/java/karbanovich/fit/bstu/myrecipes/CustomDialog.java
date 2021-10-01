package karbanovich.fit.bstu.myrecipes;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class CustomDialog extends DialogFragment {
    private DialogContexter dialogContexter;
    private DialogStatus dialogStatus;


    public CustomDialog(DialogStatus dialogStatus) {this.dialogStatus = dialogStatus;}

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        dialogContexter = (DialogContexter) context;
    }

    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        if(dialogStatus.equals(DialogStatus.DIALOG_BACK_BUTTON)) {
            builder.setIcon(android.R.drawable.ic_dialog_info);
            builder.setMessage("После выхода все введенные данные очистятся. Подтвердить?");
            builder.setNegativeButton("Отмена", null);
        }
        if(dialogStatus.equals(DialogStatus.DIALOG_DELETE_ITEM)) {
            builder.setIcon(android.R.drawable.ic_dialog_alert);
            builder.setMessage("Вы уверены что хотите удалить рецепт?");
            builder.setNegativeButton("Нет", null);
        }
        if(dialogStatus.equals(DialogStatus.DIALOG_CANCEL_CHANGES)) {
            builder.setIcon(android.R.drawable.ic_dialog_info);
            builder.setMessage("Отменить введенные изменения?");
            builder.setNegativeButton("Нет", null);
        }

        return builder
                .setTitle("Подтверждение")
                .setPositiveButton("ДА", (dialog, i) -> dialogContexter.dialogAction())
                .create();
    }
}
