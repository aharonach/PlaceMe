import React, {useEffect, useState} from 'react';
import useAxios from "../../hooks/useAxios";
import Api from "../../api";
import {Alert} from "react-bootstrap";
import Loading from "../Loading";
import Groups from "./Groups";

export default function EditGroups({ pupil }) {
    const [groups, error, loading, axiosFetch] = useAxios();
    const [updated, setUpdated] = useState(false);

    const getGroups = () => {
        axiosFetch({
            axiosInstance: Api,
            method: 'get',
            url: `/pupils/${pupil.id}/groups`,
        });
    }

    const updateGroups = data => {
        axiosFetch({
            axiosInstance: Api,
            method: 'post',
            url: `/pupils/${pupil.id}/groups`,
            data: data.groups,
        });
        setUpdated(true);
    }

    useEffect(() => {
        getGroups();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []);

    return (
        <>
            {loading && <Loading />}
            {!loading && error && <Alert variant="danger">{error}</Alert>}
            {!loading && !error && groups && <Groups pupilGroups={groups} onSubmit={updateGroups} updated={updated} />}
        </>
    );
}