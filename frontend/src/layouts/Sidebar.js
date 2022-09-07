import React from 'react';
import {
    CDBSidebar,
    CDBSidebarContent,
    CDBSidebarFooter,
    CDBSidebarHeader,
    CDBSidebarMenu,
    CDBSidebarMenuItem,
} from 'cdbreact';
import {Container, Navbar} from "react-bootstrap";
import {LinkContainer} from "react-router-bootstrap";
import {CalendarCheckFill, ClipboardCheckFill, CollectionFill, PersonFill} from "react-bootstrap-icons";
import logo from "../Logo.png"
import {useLocation} from "react-router-dom";


const Sidebar = () => {
    const { location } = useLocation();
    console.log(location);

    return (
        <div style={{ display: 'flex', height: '100vh', overflow: 'scroll initial' }}>
            <CDBSidebar  backgroundColor="#F4EFE9" style={{ width: '100%' }} minWidth={1}>
                <CDBSidebarHeader >
                    <Navbar>
                        <Container>
                            <Navbar.Brand href="#home">
                                <img
                                    src={logo}
                                    style={{ width: '100%', height:'100%' }}
                                    className="d-inline-block align-top"
                                    alt="React Bootstrap logo"
                                />
                            </Navbar.Brand>
                        </Container>
                    </Navbar>
                </CDBSidebarHeader>

                <CDBSidebarContent className="sidebar-content">
                    <CDBSidebarMenu>
                       <LinkContainer to="/pupils"  >
                           <CDBSidebarMenuItem  style={{display: 'flex'}} className="menuItem" id="pupils">
                               <PersonFill className="me-2"/> Pupils
                           </CDBSidebarMenuItem>
                       </LinkContainer>
                        <LinkContainer to="/groups">
                            <CDBSidebarMenuItem style={{display: 'flex'}} className="menuItem" id="groups">
                            <CollectionFill id="groups" className="me-2"/> Groups
                            </CDBSidebarMenuItem>
                        </LinkContainer>
                        <LinkContainer to="/templates">
                            <CDBSidebarMenuItem style={{display: 'flex'}} className="menuItem" id="templates">
                                <ClipboardCheckFill className="me-2"/> Templates
                            </CDBSidebarMenuItem>
                        </LinkContainer>
                        <LinkContainer to="/placements">
                            <CDBSidebarMenuItem style={{display: 'flex'}} className="menuItem" id="placements">
                                <CalendarCheckFill className="me-2"/> Placements
                            </CDBSidebarMenuItem>
                        </LinkContainer>

                    </CDBSidebarMenu>
                </CDBSidebarContent>

                <CDBSidebarFooter style={{ textAlign: 'left' }}>
                    <div
                        style={{
                            padding: '20px 5px',
                        }}
                    >
                        Log out
                    </div>
                </CDBSidebarFooter>
            </CDBSidebar>
        </div>
    );
};

export default Sidebar;
