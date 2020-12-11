package com.example.fitletics.fragments.homepage.dialogs

import android.content.Context
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.fitletics.R
import com.example.fitletics.activities.ActiveSessionActivity
import com.example.fitletics.models.utils.WebsiteSession
import kotlinx.android.synthetic.main.exercise_description_dialog.view.*

class ExerciseDescriptionDialog(
//    val exercise: Exercise?
    val ctx: Context,
    val title: String,
    val body: String?,
    val positive: String,
    val negative: String,
    val isExerciseDescription: Boolean
): DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var rootView: View = inflater.inflate(R.layout.exercise_description_dialog, container, false)

        val titleTextView = rootView.findViewById<TextView>(R.id.exercise_name_e_desc_text)
        val bodyTextView = rootView.findViewById<TextView>(R.id.exercise_description_e_desc_text)
        val positiveTextView = rootView.findViewById<TextView>(R.id.watch_video_e_desc_button)
        val negativeTextView = rootView.findViewById<TextView>(R.id.cancel_e_desc_button)

        titleTextView.text = title
        bodyTextView.text = body
        negativeTextView.text = negative

        if (isExerciseDescription) {
            positiveTextView.append(Html.fromHtml("<a href=\"$positive\">Watch video</a>"));
            positiveTextView.movementMethod = LinkMovementMethod.getInstance();
        }
        else{
            positiveTextView.text = positive
            positiveTextView.setOnClickListener {
                WebsiteSession(
                    ctx,
                    ActiveSessionActivity::class.java,
                    null
                );
                dismiss()
            }
        }

        rootView.cancel_e_desc_button.setOnClickListener{
            dismiss()
        }

        return rootView
    }
}