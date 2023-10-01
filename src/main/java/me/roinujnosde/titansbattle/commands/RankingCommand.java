package me.roinujnosde.titansbattle.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Dependency;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Values;
import me.roinujnosde.titansbattle.TitansBattle;
import me.roinujnosde.titansbattle.dao.ConfigurationDao;
import me.roinujnosde.titansbattle.managers.ConfigManager;
import me.roinujnosde.titansbattle.managers.DatabaseManager;
import me.roinujnosde.titansbattle.types.Group;
import me.roinujnosde.titansbattle.types.Warrior;
import me.roinujnosde.titansbattle.utils.Helper;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@CommandAlias("%titansbattle|tb")
public class RankingCommand extends BaseCommand {

    @Dependency
    private TitansBattle plugin;
    @Dependency
    private ConfigurationDao configDao;
    @Dependency
    private ConfigManager configManager;
    @Dependency
    private DatabaseManager databaseManager;

    private final int limit = configManager.getPageLimitRanking();

    private void sortGroups(final List<Group> groups, final String game, @Nullable String order) {
        groups.sort((g, g2) -> Integer.compare(g.getData().getVictories(game), g2.getData().getVictories(game)) * -1);
        if (order != null) {
            if (order.equalsIgnoreCase("kills")) {
                groups.sort((g, g2) -> Integer.compare(g.getData().getKills(game), g2.getData().getKills(game)) * -1);
            }
            if (order.equalsIgnoreCase("deaths")) {
                groups.sort((g, g2) -> Integer.compare(g.getData().getDeaths(game), g2.getData().getDeaths(game)) * -1);
            }
            if (order.equalsIgnoreCase("defeats")) {
                groups.sort((g, g2) -> Integer.compare(g.getData().getDefeats(game), g2.getData().getDefeats(game)) * -1);
            }
        }
    }

    private void sortWarriors(final List<Warrior> warriors, final String game, final @Nullable String order) {
        warriors.sort((w, w2) -> Integer.compare(w.getVictories(game), w2.getVictories(game)) * -1);
        if (order != null) {
            if (order.equalsIgnoreCase("kills")) {
                warriors.sort((w, w2) -> Integer.compare(w.getKills(game), w2.getKills(game)) * -1);
            }
            if (order.equalsIgnoreCase("deaths")) {
                warriors.sort((w, w2) -> Integer.compare(w.getDeaths(game), w2.getDeaths(game)) * -1);
            }
        }
    }

    private int getDefeatsSize(List<Group> groups, String game) {
        int defeatsSize = String.valueOf(groups.stream().mapToInt(g -> g.getData().getDefeats(game)).max()
                .orElse(0)).length();
        if (getDefeatsTitle().length() > defeatsSize) {
            defeatsSize = getDeathsTitle().length();
        }
        return defeatsSize;
    }

    private int getGroupsDeathsSize(List<Group> groups, String game) {
        int deathsSize = String.valueOf(groups.stream().mapToInt(g -> g.getData().getDeaths(game)).max().orElse(0)).length();
        if (getGroupsDeathsTitle().length() > deathsSize) {
            deathsSize = getGroupsDeathsTitle().length();
        }
        return deathsSize;
    }

    private int getGroupsKillsSize(List<Group> groups, String game) {
        int killsSize = String.valueOf(groups.stream().mapToInt(g -> g.getData().getKills(game)).max()
                .orElse(0)).length();
        if (getGroupsKillsTitle().length() > killsSize) {
            killsSize = getGroupsKillsTitle().length();
        }
        return killsSize;
    }

    private int getGroupsVictoriesSize(List<Group> groups, String game) {
        int victoriesSize = String.valueOf(groups.stream().mapToInt(g -> g.getData().getVictories(game)).max()
                .orElse(0)).length();
        if (getGroupsVictoriesTitle().length() > victoriesSize) {
            victoriesSize = getGroupsVictoriesTitle().length();
        }
        return victoriesSize;
    }

    private int getNameSize(List<Group> groups) {
        int nameSize = groups.stream().mapToInt(g -> g.getId().length()).max().orElse(0);
        if (getNameTitle().length() > nameSize) {
            nameSize = getNameTitle().length();
        }
        return nameSize;
    }

    private int getNickSize(final List<Warrior> warriors) {
        int nickSize = warriors.stream().mapToInt(w -> w.getName().length()).max().orElse(0);
        if (getNicknameTitle().length() > nickSize) {
            nickSize = getNicknameTitle().length();
        }
        return nickSize;
    }

    private int getVictoriesSize(final List<Warrior> warriors, final String game) {
        int victoriesSize = String.valueOf(warriors.stream().mapToInt(w -> w.getVictories(game)).max()
                .orElse(0)).length();
        if (getVictoriesTitle().length() > victoriesSize) {
            victoriesSize = getVictoriesTitle().length();
        }
        return victoriesSize;
    }

    private int getKillsSize(final List<Warrior> warriors, final String game) {
        int killsSize = String.valueOf(warriors.stream().mapToInt(w -> w.getKills(game)).max().orElse(0))
                .length();
        if (getKillsTitle().length() > killsSize) {
            killsSize = getKillsTitle().length();
        }
        return killsSize;
    }

    private int getDeathsSize(final List<Warrior> warriors, final String game) {
        int deathsSize = String.valueOf(warriors.stream().mapToInt(w -> w.getDeaths(game)).max().orElse(0))
                .length();
        if (getDeathsTitle().length() > deathsSize) {
            deathsSize = getDeathsTitle().length();
        }
        return deathsSize;
    }

    private String getDefeatsTitle() {
        return plugin.getLang("groups-ranking.defeats-title");
    }

    private String getGroupsDeathsTitle() {
        return plugin.getLang("groups-ranking.deaths-title");
    }

    private String getGroupsKillsTitle() {
        return plugin.getLang("groups-ranking.kills-title");
    }

    private String getGroupsVictoriesTitle() {
        return plugin.getLang("groups-ranking.victories-title");
    }

    private String getNameTitle() {
        return plugin.getLang("groups-ranking.name-title");
    }

    private String getNicknameTitle() {
        return plugin.getLang("players-ranking.nickname-title");
    }

    private String getVictoriesTitle() {
        return plugin.getLang("players-ranking.victories-title");
    }

    private String getKillsTitle() {
        return plugin.getLang("players-ranking.kills-title");
    }

    private String getDeathsTitle() {
        return plugin.getLang("players-ranking.deaths-title");
    }

    private String makeGroupTitle(List<Group> groups, String game) {
        return plugin.getLang("groups-ranking.title")
                .replaceAll("%name-title", getNameTitle())
                .replaceAll("%n-space", Helper.getSpaces(getNameSize(groups) - getNameTitle().length()))
                .replaceAll("%v-space", Helper.getSpaces(getGroupsVictoriesSize(groups, game) -
                                                         getGroupsVictoriesTitle().length()))
                .replaceAll("%v-title", getGroupsVictoriesTitle())
                .replaceAll("%k-space", Helper.getSpaces(getGroupsKillsSize(groups, game) -
                                                         getGroupsKillsTitle().length()))
                .replaceAll("%k-title", getGroupsKillsTitle())
                .replaceAll("%deaths-space", Helper.getSpaces(getGroupsDeathsSize(groups, game) -
                                                              getGroupsDeathsTitle().length()))
                .replaceAll("%deaths-title", getGroupsDeathsTitle())
                .replaceAll("%defeats-space", Helper.getSpaces(getGroupsDeathsSize(groups, game)
                                                               - getDefeatsTitle().length()))
                .replaceAll("%defeats-title", getDefeatsTitle());
    }

    private String makeWarriorTitle(final List<Warrior> warriors, final String game) {
        return plugin.getLang("players-ranking.title")
                .replaceAll("%nickname-title", getNicknameTitle())
                .replaceAll("%v-title", getVictoriesTitle())
                .replaceAll("%k-title", getKillsTitle())
                .replaceAll("%d-title", getDeathsTitle())
                .replaceAll("%n-space", Helper.getSpaces(getNickSize(warriors) - getNicknameTitle().length()))
                .replaceAll("%v-space", Helper.getSpaces(getVictoriesSize(warriors, game) - getVictoriesTitle().length()))
                .replaceAll("%k-space", Helper.getSpaces(getKillsSize(warriors, game) - getKillsTitle().length()))
                .replaceAll("%d-space", Helper.getSpaces(getDeathsSize(warriors, game) - getDeathsTitle().length()));
    }

    private String makeGroupLine(Group g, final String game, String line, int pos, List<Group> groups) {
        String name = g.getName();

        final int victories = g.getData().getVictories(game);
        final int kills = g.getData().getKills(game);
        final int deaths = g.getData().getDeaths(game);
        final int defeats = g.getData().getDefeats(game);
        return line.replaceAll("%position", String.valueOf(pos))
                .replaceAll("%name", name)
                .replaceAll("%n-space", Helper.getSpaces(getNameSize(groups) - name.length()))
                .replaceAll("%v-space", Helper.getSpaces(getGroupsVictoriesSize(groups, game) -
                                                         Helper.getLength(victories)))
                .replaceAll("%victories", String.valueOf(victories))
                .replaceAll("%k-space", Helper.getSpaces(getGroupsKillsSize(groups, game) -
                                                         Helper.getLength(kills)))
                .replaceAll("%kills", String.valueOf(kills))
                .replaceAll("%deaths-space", Helper.getSpaces(getGroupsDeathsSize(groups, game) -
                                                              Helper.getLength(deaths)))
                .replaceAll("%deaths", String.valueOf(deaths))
                .replaceAll("%defeats-space", Helper.getSpaces(getDefeatsSize(groups, game) -
                                                               Helper.getLength(defeats)))
                .replaceAll("%defeats", String.valueOf(defeats));
    }

    private String makeWarriorLine(String line, int pos, Warrior w, String game, List<Warrior> warriors) {
        String name = w.getName();
        int victories = w.getVictories(game);
        int kills = w.getKills(game);
        int deaths = w.getDeaths(game);

        return line.replaceAll("%position", String.valueOf(pos))
                .replaceAll("%nick", name)
                .replaceAll("%n-space", Helper.getSpaces(getNickSize(warriors) - name.length()))
                .replaceAll("%v-space", Helper.getSpaces(getVictoriesSize(warriors, game) - Helper.getLength(victories)))
                .replaceAll("%victories", String.valueOf(victories))
                .replaceAll("%k-space", Helper.getSpaces(getKillsSize(warriors, game) - Helper.getLength(kills)))
                .replaceAll("%kills", String.valueOf(kills))
                .replaceAll("%d-space", Helper.getSpaces(getDeathsSize(warriors, game) - Helper.getLength(deaths)))
                .replaceAll("%deaths", String.valueOf(deaths));
    }

    @Subcommand("%ranking|ranking %groups|groups")
    @CommandPermission("titansbattle.ranking")
    @CommandCompletion("@games @order_by:type=group @pages:type=group")
    @Description("{@@command.description.ranking.groups}")
    public void groupsRanking(CommandSender sender,
                              @Values("@games") String game,
                              @Values("@order_by:type=group") @Optional @Nullable String order,
                              @Optional @Default("1") int page) {

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            final List<Group> groups;
            if (plugin.getGroupManager() != null) {
                groups = new ArrayList<>(plugin.getGroupManager().getGroups());
            } else {
                groups = new ArrayList<>(0);
            }
            if (groups.isEmpty()) {
                sender.sendMessage(plugin.getLang("no-data-found"));
                return;
            }

            sortGroups(groups, game, order);

            java.util.Optional<Result> result = getResult(sender, page, groups);
            if (!result.isPresent()) return;

            if (groups.size() <= result.get().first) {
                sender.sendMessage(plugin.getLang("inexistent-page"));
                return;
            }

            String line = plugin.getLang("groups-ranking.line");
            List<String> groupsList = new ArrayList<>();

            for (int i = result.get().first; i <= result.get().last; i++) {
                int pos = i + 1;
                Group g;
                try {
                    g = groups.get(i);
                } catch (IndexOutOfBoundsException ex) {
                    g = null;
                }
                if (g == null) {
                    break;
                }
                groupsList.add(makeGroupLine(g, game, line, pos, groups));
                sender.sendMessage(makeGroupLine(g, game, line, pos, groups));
            }

            sender.sendMessage(makeGroupTitle(groups, game));
            if (!groupsList.isEmpty()) {
                for (String s : groupsList) {
                    sender.sendMessage(s);
                }
            }
        });
    }

    @Subcommand("%ranking|ranking %players|players")
    @CommandPermission("titansbattle.ranking")
    @CommandCompletion("@games @order_by:type=warrior @pages:type=warrior")
    @Description("{@@command.description.ranking.players}")
    public void playersRanking(CommandSender sender,
                               @Values("@games") String game,
                               @Values("@order_by:type=warrior") @Optional @Nullable String order,
                               @Optional @Default("1") int page) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            final List<Warrior> warriors = new ArrayList<>(databaseManager.getWarriors());
            if (warriors.isEmpty()) {
                sender.sendMessage(plugin.getLang("no-data-found"));
                return;
            }

            sortWarriors(warriors, game, order);

            java.util.Optional<Result> result = getResult(sender, page, warriors);
            if (!result.isPresent()) return;

            String line = plugin.getLang("players-ranking.line");
            List<String> warriosList = new ArrayList<>();
            for (int i = result.get().first; i <= result.get().last; i++) {
                int pos = i + 1;
                Warrior w;
                try {
                    w = warriors.get(i);
                } catch (IndexOutOfBoundsException ex) {
                    w = null;
                }
                if (w == null) {
                    break;
                }
                warriosList.add(makeWarriorLine(line, pos, w, game, warriors));
                sender.sendMessage(makeWarriorLine(line, pos, w, game, warriors));
            }

            sender.sendMessage(makeWarriorTitle(warriors, game));

            if (!warriosList.isEmpty()) {
                for (String s : warriosList) {
                    sender.sendMessage(s);
                }
            }
        });
    }

    @NotNull
    private java.util.Optional<Result> getResult(CommandSender sender, int page, @NotNull List<?> list) {
        int first = (page == 1) ? 0 : ((page - 1) * limit);
        int last = first + (limit - 1);

        if (list.size() <= first) {
            sender.sendMessage(plugin.getLang("inexistent-page"));
            return java.util.Optional.empty();
        }

        return java.util.Optional.of(new Result(first, last));
    }

    private static class Result {
        public final int first;
        public final int last;

        @Contract(pure = true)
        public Result(int first, int last) {
            this.first = first;
            this.last = last;
        }
    }
}
