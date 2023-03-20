import os
import plotly.graph_objects as go
import plotly.io as pio
import plotly.express as px

pio.kaleido.scope.mathjax = None


fig = go.Figure()

fig.add_trace(go.Bar(name="Central", x=["Central"], y=[2.982], legendrank=1, width=[0.8]
                     , text="<b>2.98GB<b>", textposition='outside', textfont = dict(size = 25)
                     , marker_color='rgb(100, 30, 22)'))
fig.add_trace(go.Bar(name="Disco", x=["Disco"], y=[3.3528], width=[0.8]
                     , text="<b>3.35GB<b>", textposition='outside', textfont = dict(size = 25)
                     , marker_color='rgb(21, 67, 97)'))
fig.add_trace(go.Bar(name="Desis", x=["Desis"], y=[2.981], width=[0.8]
                     , text="<b>2.98GB<b>", textposition='outside', textfont = dict(size = 25)
                     , marker_color='rgb(21, 90, 50)'))



#legend
fig.update_layout(showlegend=False)
fig.update_layout(
    # legend=dict(
    #     yanchor="top",
    #     y=0.99,
    #     xanchor="left",
    #     x=0.01,
    #     # bordercolor="Black",
    #     # borderwidth=2,
    #     # bgcolor="white",
    #     font=dict(
    #         size=20,
    #         color="black"
    #     ),
    # ),
    yaxis=dict(
        title_text="bytes sent in GB",
        titlefont=dict(size=35),
        ticktext=["0", "1GB", "2GB", "3GB", "4GB"],
        tickvals=[0, 1, 2, 3, 4],
        tickmode="array",
        range=[0, 4.1],
        ticks="inside",
        ticklen=20,
        tickwidth=5,
    ),
    xaxis = dict(
        # title_text="local nodes",
        # titlefont=dict(size=15),
        ticktext=["Central", "Disco", "Desis"],
        # tickvals=[1, 2, 3, 4],
        tickmode="array",
        # range=[0, 6],
    ),
)

# size & color
fig.update_layout(
    autosize=False,
    width=660,
    height=440,
    paper_bgcolor='rgba(0,0,0,0)',
    plot_bgcolor='rgba(0,0,0,0)',
    margin=dict(
        l=5,
        r=5,
        b=5,
        t=5,
        pad=0
    ),
)
# fig = px.bar(x=["a","b","c"], y=[1,3,2], color=["red", "goldenrod", "#00D"], color_discrete_map="identity")
fig.update_layout(barmode='group', bargap=0.4)

# fig.update_yaxes(automargin=True)
# fig.update_yaxes(ticks="outside", tickwidth=1, tickcolor='black', ticklen=5)
fig.update_xaxes(showline=True, linewidth=3, linecolor='black'#, mirror=True
                 , tickfont=dict(size=35))
fig.update_yaxes(showline=True, linewidth=3, linecolor='black'#, mirror=True
                 , tickfont=dict(size=35))



fig.show()
if not os.path.exists("E:\my paper\DesisPaper\Desis-Optimizing-Decentralized-Window-Aggregation\experiment\s1"):
    os.mkdir("E:\my paper\DesisPaper\Desis-Optimizing-Decentralized-Window-Aggregation\experiment\s1")
# fig.write_image("images/fig1.svg")
pio.write_image(fig, "E:\my paper\DesisPaper\Desis-Optimizing-Decentralized-Window-Aggregation\experiment\s1\/networkoverhead\/networkoverheadM.pdf")
pio.write_image(fig, "E:\my paper\DesisPaper\Desis-Optimizing-Decentralized-Window-Aggregation\experiment\s1\/networkoverhead\/networkoverheadM.svg")