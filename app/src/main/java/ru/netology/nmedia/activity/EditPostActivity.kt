package ru.netology.nmedia.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContract
import ru.netology.nmedia.databinding.ActivityEditPostBinding


class EditPostActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_POST_CONTENT = "extra_post_content"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityEditPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.content.requestFocus()


        val originalText = intent.getStringExtra(EXTRA_POST_CONTENT)
        binding.content.setText(originalText)
        binding.EditMessage.text = originalText
        val activity = this
        activity.onBackPressedDispatcher.addCallback(
            activity, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val resultIntent = Intent().apply {
                        putExtra(Intent.EXTRA_TEXT, originalText)
                    }
                    setResult(Activity.RESULT_OK, resultIntent)
                    finish()
                }
            }
        )
        binding.save.setOnClickListener {
            val editedText = binding.content.text.toString()


            if (editedText.isNotBlank()) {
                val resultIntent = Intent().apply {
                    putExtra(Intent.EXTRA_TEXT, editedText)
                }
                setResult(Activity.RESULT_OK, resultIntent)
            } else {
                setResult(Activity.RESULT_CANCELED)
            }
            finish()
        }


        binding.close.setOnClickListener {
            finish()
        }


    }

}

object EditPostContract : ActivityResultContract<String, String?>() {
    override fun createIntent(context: Context, input: String): Intent {
        return Intent(context, EditPostActivity::class.java)
            .putExtra(EditPostActivity.EXTRA_POST_CONTENT, input)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): String? {
        return intent?.getStringExtra(Intent.EXTRA_TEXT)
    }


}
