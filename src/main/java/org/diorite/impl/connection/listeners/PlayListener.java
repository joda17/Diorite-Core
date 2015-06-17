package org.diorite.impl.connection.listeners;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.diorite.impl.Main;
import org.diorite.impl.ServerImpl;
import org.diorite.impl.connection.NetworkManager;
import org.diorite.impl.connection.packets.play.PacketPlayInListener;
import org.diorite.impl.connection.packets.play.in.PacketPlayInAbilities;
import org.diorite.impl.connection.packets.play.in.PacketPlayInArmAnimation;
import org.diorite.impl.connection.packets.play.in.PacketPlayInBlockDig;
import org.diorite.impl.connection.packets.play.in.PacketPlayInBlockPlace;
import org.diorite.impl.connection.packets.play.in.PacketPlayInChat;
import org.diorite.impl.connection.packets.play.in.PacketPlayInClientCommand;
import org.diorite.impl.connection.packets.play.in.PacketPlayInCloseWindow;
import org.diorite.impl.connection.packets.play.in.PacketPlayInCustomPayload;
import org.diorite.impl.connection.packets.play.in.PacketPlayInEntityAction;
import org.diorite.impl.connection.packets.play.in.PacketPlayInFlying;
import org.diorite.impl.connection.packets.play.in.PacketPlayInHeldItemSlot;
import org.diorite.impl.connection.packets.play.in.PacketPlayInKeepAlive;
import org.diorite.impl.connection.packets.play.in.PacketPlayInLook;
import org.diorite.impl.connection.packets.play.in.PacketPlayInPosition;
import org.diorite.impl.connection.packets.play.in.PacketPlayInPositionLook;
import org.diorite.impl.connection.packets.play.in.PacketPlayInResourcePackStatus;
import org.diorite.impl.connection.packets.play.in.PacketPlayInSetCreativeSlot;
import org.diorite.impl.connection.packets.play.in.PacketPlayInSettings;
import org.diorite.impl.connection.packets.play.in.PacketPlayInTabComplete;
import org.diorite.impl.connection.packets.play.in.PacketPlayInWindowClick;
import org.diorite.impl.connection.packets.play.out.PacketPlayOutBlockChange;
import org.diorite.impl.connection.packets.play.out.PacketPlayOutDisconnect;
import org.diorite.impl.entity.PlayerImpl;
import org.diorite.impl.input.InputAction;
import org.diorite.impl.input.InputActionType;
import org.diorite.BlockLocation;
import org.diorite.chat.ChatPosition;
import org.diorite.chat.component.BaseComponent;
import org.diorite.material.Material;
import org.diorite.world.World;

public class PlayListener implements PacketPlayInListener
{
    private final ServerImpl     server;
    private final NetworkManager networkManager;
    private final PlayerImpl     player;

    public PlayListener(final ServerImpl server, final NetworkManager networkManager, final PlayerImpl player)
    {
        this.server = server;
        this.networkManager = networkManager;
        this.player = player;
    }


    @Override
    public void handle(final PacketPlayInKeepAlive packet)
    {
        this.networkManager.updateKeepAlive();
    }

    @Override
    public void handle(final PacketPlayInSettings packet)
    {
//        final byte oldViewDistance = this.player.getViewDistance();
        this.player.setViewDistance(packet.getViewDistance());
//        if (oldViewDistance != this.player.getViewDistance())
//        {
//            this.player.getPlayerChunks().wantUpdate();
//        }
        // TODO: implement
    }

    @Override
    public void handle(final PacketPlayInCustomPayload packet)
    {
        // TODO: implement
    }

    @Override
    public void handle(final PacketPlayInHeldItemSlot packet)
    {
        // TODO: implement
    }

    @Override
    public void handle(final PacketPlayInPositionLook packet)
    {
        this.player.setPositionAndRotation(packet.getX(), packet.getY(), packet.getZ(), packet.getYaw(), packet.getPitch());
    }

    @Override
    public void handle(final PacketPlayInFlying packet)
    {
        // TODO: implement
    }

    @Override
    public void handle(final PacketPlayInPosition packet)
    {
        this.player.setPosition(packet.getX(), packet.getY(), packet.getZ());
    }

    @Override
    public void handle(final PacketPlayInLook packet)
    {
        this.player.setRotation(packet.getYaw(), packet.getPitch());
    }

    @Override
    public void handle(final PacketPlayInChat packet)
    {
        final String str = packet.getContent();
        //noinspection HardcodedFileSeparator
        if (str.startsWith("/"))
        {
            this.server.getInputThread().add(new InputAction(str.substring(1), this.player, InputActionType.COMMAND));
        }
        else
        {
            this.server.getInputThread().add(new InputAction(str, this.player, InputActionType.CHAT));
        }
    }

    @Override
    public void handle(final PacketPlayInTabComplete packet)
    {
        this.server.getInputThread().add(new InputAction(packet.getContent(), this.player, InputActionType.TAB_COMPLETE));
    }

    @Override
    public void handle(final PacketPlayInAbilities packet)
    {
        this.player.setAbilities(packet);
    }

    @Override
    public void handle(final PacketPlayInResourcePackStatus packet)
    {
        // TODO This is not needed? Maybe create event or something other...
    }

    @Override
    public void handle(final PacketPlayInSetCreativeSlot packet)
    {
        Main.debug("creative slot: " + packet.getSlot() + ", item: " + packet.getItem());
        // TODO: meh.
    }

    @Override
    public void handle(final PacketPlayInEntityAction packet)
    {
        packet.getEntityAction().doAction(this.player, packet.getJumpBoost());
    }

    @Override
    public void handle(final PacketPlayInArmAnimation packet)
    {
        // TODO: implement
    }

    @Override
    public void handle(final PacketPlayInBlockDig packet)
    {
        if (packet.getAction() == PacketPlayInBlockDig.BlockDigAction.FINISH_DIG)
        {
            final World world = this.player.getWorld();
            final BlockLocation loc = packet.getBlockLocation().setWorld(world);
//            world.submitAction(loc.getChunkPos(), () -> { TODO: do it right
                world.setBlock(loc, Material.AIR);
                this.server.getPlayersManager().forEach(p -> p.getWorld().equals(world), new PacketPlayOutBlockChange(loc, Material.AIR));
//            });
        }
        // TODO: implement
    }

    @Override
    public void handle(final PacketPlayInBlockPlace packet)
    {
        //   ChunkMultithreadedHandler.add(new BlockPlaceAction(packet.getLocation(), , this.player));
        // TODO: implement
    }

    @Override
    public void handle(final PacketPlayInClientCommand packet)
    {
        // TODO: implement
    }

    @Override
    public void handle(final PacketPlayInCloseWindow packet)
    {
        Main.debug("Close windows: " + packet.getId());
        // TODO: implement
    }

    @Override
    public void handle(final PacketPlayInWindowClick packet)
    {
        Main.debug("Click (" + packet.getId() + ") slot: " + packet.getClickedSlot() + ", type: " + packet.getClickType() + ", action: " + packet.getActionNumber() + ", item: " + packet.getClicked());
        // TODO: implement
    }

    public NetworkManager getNetworkManager()
    {
        return this.networkManager;
    }

    public ServerImpl getServer()
    {
        return this.server;
    }

    @Override
    public void disconnect(final BaseComponent message)
    {
        this.server.getPlayersManager().playerQuit(this.player);

        this.networkManager.sendPacket(new PacketPlayOutDisconnect(message));
        this.networkManager.close(message, true);
        this.server.getServerConnection().remove(this.networkManager);

        this.server.broadcastSimpleColoredMessage(ChatPosition.ACTION, "&3&l" + this.player.getName() + "&7&l left from the server!");
        this.server.broadcastSimpleColoredMessage(ChatPosition.SYSTEM, "&3" + this.player.getName() + "&7 left from the server!");
//        this.server.sendConsoleSimpleColoredMessage("&3" + this.player.getName() + " &7left the server. (" + message.toLegacyText() + "&7)");

//        this.player.getWorld().removeEntity(this.player); // TODO re-add, or something
        // TODO: implement
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("server", this.server).append("networkManager", this.networkManager).toString();
    }

    public PlayerImpl getPlayer()
    {
        return this.player;
    }
}
