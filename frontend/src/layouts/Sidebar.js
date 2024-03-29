import React from 'react';
import {
    CDBSidebar,
    CDBSidebarContent,
    CDBSidebarHeader,
    CDBSidebarMenu,
    CDBSidebarMenuItem,
} from 'cdbreact';
import {LinkContainer} from "react-router-bootstrap";
import {CalendarCheckFill, ClipboardCheckFill, CollectionFill, GearFill, PersonFill} from "react-bootstrap-icons";
import logo from "../Logo.png"
import {NavLink, useResolvedPath} from "react-router-dom";

const menu = [
    {
        label: 'Pupils',
        path: '/pupils',
        icon: <PersonFill />
    },
    {
        label: 'Groups',
        path: '/groups',
        icon: <CollectionFill />
    },
    {
        label: 'Templates',
        path: '/templates',
        icon: <ClipboardCheckFill />
    },
    {
        label: 'Placements',
        path: '/placements',
        icon: <CalendarCheckFill />
    },
    {
        label: "EA Configs",
        path: '/configs',
        icon: <GearFill />
    }
];

const pathIncludes = ( path, includes ) => {
    return window.location.pathname.startsWith(includes);
};

const Sidebar = () => {
    const href = useResolvedPath(window.location.pathname).pathname;

    return (
        <CDBSidebar backgroundColor="#F4EFE9">
            <CDBSidebarHeader style={{ border: 0 }}>
                <NavLink to="/">
                    <img className="img-fluid" src={logo} alt="PlaceMe" />
                </NavLink>
            </CDBSidebarHeader>
            <CDBSidebarContent className="sidebar-content">
                <CDBSidebarMenu>
                    {menu.map( menuItem => (
                        <LinkContainer key={menuItem.path} to={menuItem.path} isActive={pathIncludes(href, menuItem.path)}>
                            <CDBSidebarMenuItem className="menuItem" id={menuItem.label.toLowerCase().replace(' ','-')}>
                                {menuItem.icon} {menuItem.label}
                            </CDBSidebarMenuItem>
                        </LinkContainer>
                    ))}
                </CDBSidebarMenu>
            </CDBSidebarContent>
        </CDBSidebar>
    );
};

export default Sidebar;
