package com.aliucord.plugins;

import android.content.Context;
import android.util.Pair;

import androidx.annotation.NonNull;

import com.aliucord.Constants;
import com.aliucord.annotations.AliucordPlugin;
import com.aliucord.api.CommandsAPI;
import com.aliucord.entities.Plugin;
import com.aliucord.patcher.Hook;
import com.aliucord.patcher.PreHook;
import com.aliucord.utils.ReflectUtils;
import com.discord.api.message.Message;
import com.discord.models.domain.ModelUserSettings;
import com.discord.stores.StoreStream;
import com.discord.widgets.chat.MessageContent;
import com.discord.widgets.chat.MessageManager;
import com.discord.widgets.chat.input.ChatInputViewModel;
import com.discord.widgets.settings.WidgetSettingsAppearance;
import com.discord.widgets.settings.WidgetSettingsAppearance$updateTheme$1;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

import kotlin.jvm.functions.Function1;

@SuppressWarnings("unused")
@AliucordPlugin
public class GoodPerson extends Plugin {

    List<String> badVerbs  = Arrays.asList("fuck","cum","kill","censor","destroy","replace","ingest");
    List<String> badNameSingular  = Arrays.asList("man","woman","nerd");
    List<String> badNamePlural  = Arrays.aslist("men","women","fuckers");
    List<String> badNouns = Arrays.asList("cunt","shit","bullshit","ass","bitch","nigga","hell","whore","dick","piss","pussy","slut",
            "tit","fag","cum","cock","retard","blowjob","bastard","kotlin","die","sex","nigger","gay","brainrot","brainless","nut",
            "damn","deez","breed","allocate","date");

    List<String> badVerbReplacements  = Arrays.asList("love","eat","deconstruct","marry","fart","teach","display","plug",
            "undress","finish","freeze","beat","free","brush","melt","educate","injure","change",
            "hit video game Five Nights at Freddy's");
            
    List<String> badNameSungularReplacements  = Arrays.asList("individual standing over to the side with a suspicious bowtie",
            "doctor who (2010)","android","gyndroid","@fredboat♪♪#7284","burrito","taco","@REDWAV5#2600");
            
    List<String> badNamePluralReplacements  = Arrays.asList("individuals standing over to the side with suspicious bowties",
            "@everyone","copious numbers of individuals);

    List<String> badNounReplacements = Arrays.asList("google.com","youtube.com","Pokémon Mystery Dungeon: Stargazer Chronicles",
            "Pokémon Mystery Dungeon: R&D","Noun#523","[INSERT POSITIVE WORD IDFK]","hat","hair",":fish:",":thumbsup:",":x:",":cat:",
            ""Spring in my Step" by Silent Partner");

    public boolean isBadNoun(String noun) {
        noun = noun.toLowerCase();
        //returns boolean and replacement
        for (var badNoun: badNouns) {
            if (noun.startsWith(badNoun)) return true;
        }
        return false;
    }

    public boolean isBadVerb(String verb) {
        verb = verb.toLowerCase();
        //returns boolean and word
        for (var badVerb: badVerbs) {
            if (verb.length() > 1 &&verb.contains(badVerb)) return true;
        }
        return false;
    }

    public String getRandomVerb(){
        return badVerbReplacements.get(new Random().nextInt(badVerbReplacements.size() - 1));
    }

    public String getRandomNoun(){
        return badNounReplacements.get(new Random().nextInt(badNounReplacements.size() - 1));
    }

    public String filterWord(String word) {
        if (word.contains("http")) return null;
        if (isBadVerb(word) && isBadNoun(word)) {
            if (new Random().nextBoolean()) {
                return getRandomNoun();
            } else {
                return  getRandomVerb();
            }
        } else if (isBadVerb(word)) {
            return getRandomVerb();
        } else if (isBadNoun(word)) {
            return getRandomNoun();
        }

        return null;
    }

    @Override
    public void start(Context context) throws NoSuchMethodException {
        patcher.patch(ChatInputViewModel.class.getDeclaredMethod("sendMessage", Context.class, MessageManager.class, MessageContent.class, List.class, boolean.class, Function1.class),
                new PreHook(cf -> {
                    var thisobj = (ChatInputViewModel) cf.thisObject;
                    var content = (MessageContent) cf.args[2];
                    try {
                        var mes = content.component1().trim() +" ";

                        for (var word : mes.split(" ")) {
                            String filteredWord = filterWord(word);
                            if (filteredWord != null) mes = mes.replaceFirst(word,filteredWord);
                        }

                        ReflectUtils.setField(content, "textContent", mes);
                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }));
    }

    @Override
    public void stop(Context context) {
        patcher.unpatchAll();
        commands.unregisterAll();
    }
}
