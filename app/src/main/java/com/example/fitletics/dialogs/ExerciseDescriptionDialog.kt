package com.example.fitletics.dialogs

import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.fitletics.R
import com.example.fitletics.models.Exercise
import kotlinx.android.synthetic.main.exercise_description_dialog.view.*

class ExerciseDescriptionDialog(val exercise: Exercise? ): DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var rootView: View = inflater.inflate(R.layout.exercise_description_dialog, container, false)

        val name = rootView.findViewById<TextView>(R.id.exercise_name_e_desc_text)
        val description = rootView.findViewById<TextView>(R.id.exercise_description_e_desc_text)
        val link = rootView.findViewById<TextView>(R.id.watch_video_e_desc_button)

        name.text = exercise?.name
        description.text = exercise?.description
        val weblink = exercise?.link

        link.append(Html.fromHtml("<a href=\"$weblink\">Watch video</a>"));
        link.movementMethod = LinkMovementMethod.getInstance();

        rootView.cancel_e_desc_button.setOnClickListener{
            dismiss()
        }

        return rootView
    }
}