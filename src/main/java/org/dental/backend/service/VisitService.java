package org.dental.backend.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dental.backend.bot.BotContext;
import org.dental.backend.bot.BotState;
import org.dental.backend.bot.ChatBot;
import org.dental.backend.dto.AppUserPatchDTO;
import org.dental.backend.dto.AppUserReadDTO;
import org.dental.backend.repository.VisitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VisitService {

    @Autowired
    private VisitRepository visitRepository;

    @Autowired
    private AppUserService appUserService;

    private static final Logger LOGGER = LogManager.getLogger(VisitService.class);

    public void createVisit(ChatBot bot, AppUserReadDTO user, String text) {
        BotContext context;
        BotState state;

        context = BotContext.of(bot, user, text);
        state = BotState.getStateById(user.getStateId());

        LOGGER.info("Update received for user in state: " + state);

        // handle input for personal user state
        state.handleInput(context);

        do {
            state = state.nextState(); // got to next state
            state.enter(context);      // enter to next state
        } while (!state.isInputNeeded());

        AppUserPatchDTO patchDTO = new AppUserPatchDTO();
        patchDTO.setStateId(state.ordinal());

        appUserService.updateUser(user.getChatId(), patchDTO);
    }
}
