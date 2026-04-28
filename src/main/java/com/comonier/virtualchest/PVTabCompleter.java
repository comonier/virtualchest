package com.comonier.virtualchest;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PVTabCompleter implements TabCompleter {

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.add("reload");
            if (sender.hasPermission("virtualchest.admin")) {
                completions.add("admin");
                completions.add("list");
            }
            for (int i = 1; i <= 5; i++) completions.add(String.valueOf(i));
            return completions.stream().filter(s -> s.startsWith(args[0].toLowerCase())).collect(Collectors.toList());
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("admin") && sender.hasPermission("virtualchest.admin")) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("admin") && sender.hasPermission("virtualchest.admin")) {
            completions.addAll(Arrays.asList("1", "2", "3", "4", "5"));
            return completions.stream().filter(s -> s.startsWith(args[2].toLowerCase())).collect(Collectors.toList());
        }

        return completions;
    }
}
