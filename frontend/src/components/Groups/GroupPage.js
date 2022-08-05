import React, {useEffect, useState} from 'react';
import {useParams, useNavigate} from "react-router-dom";
import useAxios from "../../hooks/useAxios";
import Api from "../../api";
import Loading from "../Loading";
import {Alert, Button} from "react-bootstrap";
import EditGroup from "./EditGroup";

export default function GroupPage() {
    let { groupId } = useParams();
    const [group, error, loading, axiosFetch] = useAxios();
    const [deleted, setDeleted] = useState(false);
    let navigate = useNavigate();

    deleted && navigate('/groups', {replace: true});

    const getGroup = () => {
        axiosFetch({
            axiosInstance: Api,
            method: 'get',
            url: `/groups/${groupId}`,
        });
    }

    const handleDelete = () => {
        axiosFetch({
            axiosInstance: Api,
            method: 'delete',
            url: `/groups/${groupId}`,
        }).then(() => setDeleted(true));
    }

    useEffect(() => {
        getGroup();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []);

    return (
        <>
            {loading && <Loading />}
            {!loading && error && <Alert variant="danger">{error}</Alert>}
            {!loading && !error && group &&
                <article className="group">
                    <h2>{group.name} (ID: {group.id})</h2>
                    <Button variant="danger" onClick={handleDelete}>Delete Group</Button>
                    <EditGroup group={group} />
                </article>
            }
        </>
    )
}
