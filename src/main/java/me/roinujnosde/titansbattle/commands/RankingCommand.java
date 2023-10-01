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
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import me.roinujnosde.titansbattle.TitansBattle;
import me.roinujnosde.titansbattle.managers.ConfigManager;
import me.roinujnosde.titansbattle.managers.DatabaseManager;
import me.roinujnosde.titansbattle.types.Group;
import me.roinujnosde.titansbattle.types.Warrior;
import me.roinujnosde.titansbattle.utils.Helper;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@CommandAlias("%titansbattle|tb")
@Subcommand("%ranking|ranking")
public class RankingCommand extends BaseCommand {

    @Dependency
    private TitansBattle plugin;
    @Dependency
    private ConfigManager configManager;
    @Dependency
    private DatabaseManager databaseManager;

    private final RankingCache rankingCache = new RankingCache();

    private void sortGroups(final @NotNull List<Group> groups, final String game, @NotNull String order) {
        switch (order) {
            case "kills":
                groups.sort((g, g2) -> Integer.compare(g.getData().getKills(game), g2.getData().getKills(game)) * -1);
                break;
            case "deaths":
                groups.sort((g, g2) -> Integer.compare(g.getData().getDeaths(game), g2.getData().getDeaths(game)) * -1);
                break;
            case "defeats":
                groups.sort((g, g2) -> Integer.compare(g.getData().getDefeats(game), g2.getData().getDefeats(game)) * -1);
                break;
            default:
                groups.sort((g, g2) -> Integer.compare(g.getData().getVictories(game), g2.getData().getVictories(game)) * -1);
        }
    }

    private void sortWarriors(final @NotNull List<Warrior> warriors, final String game, final @NotNull String order) {
        switch (order) {
            case "kills":
                warriors.sort((w, w2) -> Integer.compare(w.getKills(game), w2.getKills(game)) * -1);
                break;
            case "deaths":
                warriors.sort((w, w2) -> Integer.compare(w.getDeaths(game), w2.getDeaths(game)) * -1);
                break;
            default:
                warriors.sort((w, w2) -> Integer.compare(w.getVictories(game), w2.getVictories(game)) * -1);
        }
    }

    private int getDefeatsSize(@NotNull List<Group> groups, String game) {
        int defeatsSize = String.valueOf(groups.stream().mapToInt(g -> g.getData().getDefeats(game)).max()
                .orElse(0)).length();
        if (getDefeatsTitle().length() > defeatsSize) {
            defeatsSize = getDeathsTitle().length();
        }
        return defeatsSize;
    }

    private int getGroupsDeathsSize(@NotNull List<Group> groups, String game) {
        int deathsSize = String.valueOf(groups.stream().mapToInt(g -> g.getData().getDeaths(game)).max().orElse(0)).length();
        if (getGroupsDeathsTitle().length() > deathsSize) {
            deathsSize = getGroupsDeathsTitle().length();
        }
        return deathsSize;
    }

    private int getGroupsKillsSize(@NotNull List<Group> groups, String game) {
        int killsSize = String.valueOf(groups.stream().mapToInt(g -> g.getData().getKills(game)).max()
                .orElse(0)).length();
        if (getGroupsKillsTitle().length() > killsSize) {
            killsSize = getGroupsKillsTitle().length();
        }
        return killsSize;
    }

    private int getGroupsVictoriesSize(@NotNull List<Group> groups, String game) {
        int victoriesSize = String.valueOf(groups.stream().mapToInt(g -> g.getData().getVictories(game)).max()
                .orElse(0)).length();
        if (getGroupsVictoriesTitle().length() > victoriesSize) {
            victoriesSize = getGroupsVictoriesTitle().length();
        }
        return victoriesSize;
    }

    private int getNameSize(@NotNull List<Group> groups) {
        int nameSize = groups.stream().mapToInt(g -> g.getId().length()).max().orElse(0);
        if (getNameTitle().length() > nameSize) {
            nameSize = getNameTitle().length();
        }
        return nameSize;
    }

    private int getNickSize(final @NotNull List<Warrior> warriors) {
        int nickSize = warriors.stream().mapToInt(w -> w.getName().length()).max().orElse(0);
        if (getNicknameTitle().length() > nickSize) {
            nickSize = getNicknameTitle().length();
        }
        return nickSize;
    }

    private int getVictoriesSize(final @NotNull List<Warrior> warriors, final String game) {
        int victoriesSize = String.valueOf(warriors.stream().mapToInt(w -> w.getVictories(game)).max()
                .orElse(0)).length();
        if (getVictoriesTitle().length() > victoriesSize) {
            victoriesSize = getVictoriesTitle().length();
        }
        return victoriesSize;
    }

    private int getKillsSize(final @NotNull List<Warrior> warriors, final String game) {
        int killsSize = String.valueOf(warriors.stream().mapToInt(w -> w.getKills(game)).max().orElse(0))
                .length();
        if (getKillsTitle().length() > killsSize) {
            killsSize = getKillsTitle().length();
        }
        return killsSize;
    }

    private int getDeathsSize(final @NotNull List<Warrior> warriors, final String game) {
        int deathsSize = String.valueOf(warriors.stream().mapToInt(w -> w.getDeaths(game)).max().orElse(0))
                .length();
        if (getDeathsTitle().length() > deathsSize) {
            deathsSize = getDeathsTitle().length();
        }
        return deathsSize;
    }

    private @NotNull String getDefeatsTitle() {
        return plugin.getLang("groups-ranking.defeats-title");
    }

    private @NotNull String getGroupsDeathsTitle() {
        return plugin.getLang("groups-ranking.deaths-title");
    }

    private @NotNull String getGroupsKillsTitle() {
        return plugin.getLang("groups-ranking.kills-title");
    }

    private @NotNull String getGroupsVictoriesTitle() {
        return plugin.getLang("groups-ranking.victories-title");
    }

    private @NotNull String getNameTitle() {
        return plugin.getLang("groups-ranking.name-title");
    }

    private @NotNull String getNicknameTitle() {
        return plugin.getLang("players-ranking.nickname-title");
    }

    private @NotNull String getVictoriesTitle() {
        return plugin.getLang("players-ranking.victories-title");
    }

    private @NotNull String getKillsTitle() {
        return plugin.getLang("players-ranking.kills-title");
    }

    private @NotNull String getDeathsTitle() {
        return plugin.getLang("players-ranking.deaths-title");
    }

    private @NotNull String makeGroupTitle(List<Group> groups, String game) {
        return plugin.getLang("groups-ranking.title")
                .replace("%name-title", getNameTitle())
                .replace("%n-space", Helper.getSpaces(getNameSize(groups) - getNameTitle().length()))
                .replace("%v-space", Helper.getSpaces(getGroupsVictoriesSize(groups, game) -
                                                      getGroupsVictoriesTitle().length()))
                .replace("%v-title", getGroupsVictoriesTitle())
                .replace("%k-space", Helper.getSpaces(getGroupsKillsSize(groups, game) -
                                                      getGroupsKillsTitle().length()))
                .replace("%k-title", getGroupsKillsTitle())
                .replace("%deaths-space", Helper.getSpaces(getGroupsDeathsSize(groups, game) -
                                                           getGroupsDeathsTitle().length()))
                .replace("%deaths-title", getGroupsDeathsTitle())
                .replace("%defeats-space", Helper.getSpaces(getGroupsDeathsSize(groups, game)
                                                            - getDefeatsTitle().length()))
                .replace("%defeats-title", getDefeatsTitle());
    }

    private @NotNull String makeWarriorTitle(final List<Warrior> warriors, final String game) {
        return plugin.getLang("players-ranking.title")
                .replace("%nickname-title", getNicknameTitle())
                .replace("%v-title", getVictoriesTitle())
                .replace("%k-title", getKillsTitle())
                .replace("%d-title", getDeathsTitle())
                .replace("%n-space", Helper.getSpaces(getNickSize(warriors) - getNicknameTitle().length()))
                .replace("%v-space", Helper.getSpaces(getVictoriesSize(warriors, game) - getVictoriesTitle().length()))
                .replace("%k-space", Helper.getSpaces(getKillsSize(warriors, game) - getKillsTitle().length()))
                .replace("%d-space", Helper.getSpaces(getDeathsSize(warriors, game) - getDeathsTitle().length()));
    }

    private @NotNull String makeGroupLine(@NotNull Group g, final String game, @NotNull String line, int pos, List<Group> groups) {
        String name = g.getName();

        final int victories = g.getData().getVictories(game);
        final int kills = g.getData().getKills(game);
        final int deaths = g.getData().getDeaths(game);
        final int defeats = g.getData().getDefeats(game);
        return line.replace("%position", String.format("%02d", pos))
                .replace("%name", name)
                .replace("%n-space", Helper.getSpaces(getNameSize(groups) - name.length()))
                .replace("%v-space", Helper.getSpaces(getGroupsVictoriesSize(groups, game) -
                                                      Helper.getLength(victories)))
                .replace("%victories", String.valueOf(victories))
                .replace("%k-space", Helper.getSpaces(getGroupsKillsSize(groups, game) -
                                                      Helper.getLength(kills)))
                .replace("%kills", String.valueOf(kills))
                .replace("%deaths-space", Helper.getSpaces(getGroupsDeathsSize(groups, game) -
                                                           Helper.getLength(deaths)))
                .replace("%deaths", String.valueOf(deaths))
                .replace("%defeats-space", Helper.getSpaces(getDefeatsSize(groups, game) -
                                                            Helper.getLength(defeats)))
                .replace("%defeats", String.valueOf(defeats));
    }

    private @NotNull String makeWarriorLine(@NotNull String line, int pos, @NotNull Warrior w, String game, List<Warrior> warriors) {
        String name = w.getName();
        int victories = w.getVictories(game);
        int kills = w.getKills(game);
        int deaths = w.getDeaths(game);

        return line.replace("%position", String.format("%02d", pos))
                .replace("%nick", name)
                .replace("%n-space", Helper.getSpaces(getNickSize(warriors) - name.length()))
                .replace("%v-space", Helper.getSpaces(getVictoriesSize(warriors, game) - Helper.getLength(victories)))
                .replace("%victories", String.valueOf(victories))
                .replace("%k-space", Helper.getSpaces(getKillsSize(warriors, game) - Helper.getLength(kills)))
                .replace("%kills", String.valueOf(kills))
                .replace("%d-space", Helper.getSpaces(getDeathsSize(warriors, game) - Helper.getLength(deaths)))
                .replace("%deaths", String.valueOf(deaths));
    }

    public void sendNavBar(CommandSender sender, String options, int page, int maxPage) {
        // TODO: fetch from language file
        TextComponent message = new TextComponent();
        String command = String.format("/tb ranking %s ", options);

        TextComponent previousPage = new TextComponent("[Página Anterior]");
        int previousPageNumber = page <= 1 ? 1 : page - 1;
        previousPage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command + previousPageNumber));

        TextComponent nextPage = new TextComponent("[Próxima Página]");
        int nextPageNumber = page >= maxPage ? maxPage : page + 1;
        nextPage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command + nextPageNumber));

        message.addExtra(previousPage);
        message.addExtra(" Página " + page + " de " + maxPage + " ");
        message.addExtra(nextPage);

        if (sender instanceof Player) {
            Player player = (Player) sender;
            player.spigot().sendMessage(message);
        } else {
            sender.sendMessage(message.getText());
        }
    }

    @Subcommand("%groups|groups")
    @CommandPermission("titansbattle.ranking")
    @CommandCompletion("@games @order_by:type=group @pages:type=group")
    @Description("{@@command.description.ranking.groups}")
    public void groupsRanking(CommandSender sender,
                              @Values("@games") String game,
                              @Values("@order_by:type=group") @Default("wins") @Optional @Nullable String order,
                              @Optional @Default("1") int page) {
        // TODO: add loading message?

        final String finalOrder = order == null ? "wins" : order;
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            List<String> groupsList;
            String cacheKey = String.format("%s-%s-%s-%d", "groups", game, finalOrder, page);
            plugin.getLogger().info("cacheKey: " + cacheKey);
            if (rankingCache.get(cacheKey) == null) {
                plugin.getLogger().info("cacheKey not found");
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

                sortGroups(groups, game, finalOrder);

                java.util.Optional<Result> result = getResult(sender, page, groups);
                if (!result.isPresent()) return;

                if (groups.size() <= result.get().first) {
                    sender.sendMessage(plugin.getLang("inexistent-page"));
                    return;
                }

                String line = plugin.getLang("groups-ranking.line");
                groupsList = new ArrayList<>();
                groupsList.add(makeGroupTitle(groups, game));
                for (int i = result.get().first; i <= result.get().last; i++) {
                    int pos = i + 1;
                    if (i >= groups.size()) break;
                    Group group = groups.get(i);
                    groupsList.add(makeGroupLine(group, game, line, pos, groups));
                }
                rankingCache.put(cacheKey, groupsList);
            } else {
                plugin.getLogger().info("cacheKey found");
                groupsList = rankingCache.get(cacheKey);
            }

            if (groupsList != null && !groupsList.isEmpty()) {
                for (String s : groupsList) {
                    sender.sendMessage(s);
                }

                String options = String.format("%s %s %s", "groups", game, finalOrder);
                sendNavBar(sender, options, page, databaseManager.getGroups().size() / configManager.getPageLimitRanking() + 1);
            }
        });
    }

    @Subcommand("%players|players")
    @CommandPermission("titansbattle.ranking")
    @CommandCompletion("@games @order_by:type=warrior @pages:type=warrior")
    @Description("{@@command.description.ranking.player}")
    public void playersRanking(CommandSender sender,
                               @Values("@games") String game,
                               @Values("@order_by:type=warrior") @Default("wins") @Optional @Nullable String order,
                               @Optional @Default("1") int page) {
        // TODO: add loading message?

        final String finalOrder = order == null ? "wins" : order;
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            List<String> warriosList;
            String cacheKey = String.format("%s-%s-%s-%d", "warriors", game, finalOrder, page);
            plugin.getLogger().info("cacheKey: " + cacheKey);
            if (rankingCache.get(cacheKey) == null) {
                plugin.getLogger().info("cacheKey not found");
                final List<Warrior> warriors = new ArrayList<>(databaseManager.getWarriors());

                if (warriors.isEmpty()) {
                    sender.sendMessage(plugin.getLang("no-data-found"));
                    return;
                }

                sortWarriors(warriors, game, finalOrder);

                java.util.Optional<Result> result = getResult(sender, page, warriors);
                if (!result.isPresent()) return;

                String line = plugin.getLang("players-ranking.line");
                warriosList = new ArrayList<>();
                warriosList.add(makeWarriorTitle(warriors, game));
                for (int i = result.get().first; i <= result.get().last; i++) {
                    int pos = i + 1;
                    if (i >= warriors.size()) break;
                    Warrior warrior = warriors.get(i);
                    warriosList.add(makeWarriorLine(line, pos, warrior, game, warriors));
                }
                rankingCache.put(cacheKey, warriosList);
            } else {
                plugin.getLogger().info("cacheKey found");
                warriosList = rankingCache.get(cacheKey);
            }

            if (warriosList != null && !warriosList.isEmpty()) {
                for (String s : warriosList) {
                    sender.sendMessage(s);
                }

                String options = String.format("%s %s %s", "players", game, finalOrder);
                sendNavBar(sender, options, page, databaseManager.getWarriors().size() / configManager.getPageLimitRanking() + 1);
            }
        });
    }

    @NotNull
    private java.util.Optional<Result> getResult(CommandSender sender, int page, @NotNull List<?> list) {
        int limit = configManager.getPageLimitRanking();
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

    @SuppressWarnings("UnstableApiUsage")
    static class RankingCache {

        private final Cache<String, List<String>> cache;

        public RankingCache() {
            cache = CacheBuilder.newBuilder()
                    .concurrencyLevel(4)
                    // TODO: fetch from config?
                    .expireAfterWrite(300, TimeUnit.SECONDS)
                    .build();
        }

        public void put(String key, List<String> value) {
            cache.put(key, value);
        }

        @Nullable
        public List<String> get(String key) {
            return cache.getIfPresent(key);
        }
    }
}
