package ro.boa.clinic.service;

import com.theokanning.openai.OpenAiHttpException;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ro.boa.clinic.repository.DoctorRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class SpecializationService {
    @Value("${openai.key}")
    private String openAiKey;

    private final DoctorRepository doctorRepository;

    private final static String SETUP_PROMPT =
        "You are a medical specialization extractor. You will receive a description of symptoms, and you will respond" +
            " only with a specialization that best matches the description. You can only choose a specialization from" +
            " the list that I provide. If none of the specializations match, respond simply \"null\". The list of " +
            "specializations from which you can choose is: ";

    public List<String> getSpecializations() {
        return doctorRepository.listAllSpecializations();
    }

    public Optional<String> detectSpecialization(String description) {
        OpenAiService service = new OpenAiService(openAiKey);

        List<String> possibleSpecializations = listAllSpecializations();

        ChatCompletionRequest completionRequest = ChatCompletionRequest.builder()
            .model("gpt-3.5-turbo")
            .messages(buildModelSetupMessages(possibleSpecializations, description))
            .build();

        try {
            ChatCompletionResult result = service.createChatCompletion(completionRequest);
            String response = result.getChoices().getFirst().getMessage().getContent();
            return possibleSpecializations.contains(response) ? Optional.of(response) : Optional.empty();
        } catch (OpenAiHttpException e) {
            return Optional.empty();
        }
    }

    public List<String> listAllSpecializations() {
        return doctorRepository.listAllSpecializations();
    }

    private List<ChatMessage> buildModelSetupMessages(List<String> specializations, String description) {
        String specializationList = String.join(", ", specializations);

        List<ChatMessage> MODEL_SETUP_MESSAGES = List.of(
            new ChatMessage(ChatMessageRole.SYSTEM.value(), SETUP_PROMPT + specializationList),
            new ChatMessage(
                ChatMessageRole.USER.value(),
                "I feel pain in my right ear. It's pulsating and hurts more when I lie down."
            ),
            new ChatMessage(ChatMessageRole.ASSISTANT.value(), "otolaryngology")
        );

        ArrayList<ChatMessage> systemMessages = new ArrayList<>(MODEL_SETUP_MESSAGES);
        systemMessages.add(new ChatMessage(ChatMessageRole.USER.value(), description));

        return systemMessages;
    }
}