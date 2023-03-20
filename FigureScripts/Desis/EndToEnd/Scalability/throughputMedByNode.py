import os
import plotly.graph_objects as go
import plotly.io as pio
import plotly.express as px
import os
import sys
# Append parent directory to import path
sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
import config
pio.kaleido.scope.mathjax = None


fig = go.Figure()

fig.add_trace(go.Bar(name="Root", x=["Root"], y=[7441958], legendrank=1, width=[0.8]
                     , text="<b>7.44M<b>", textposition='outside', textfont = dict(size = 25)
                     , marker_color=config.desis))
fig.add_trace(go.Bar(name="Intermediate", x=["Intermediate"], y=[16301088.86], legendrank=2, width=[0.8]
                     , text="<b>16.23<b>", textposition='outside', textfont = dict(size = 25)
                     , marker_color=config.disco))
fig.add_trace(go.Bar(name="Local", x=["Local"], y=[7361659.16], legendrank=4, width=[0.8]
                     , text="<b>7.36M<b>", textposition='outside', textfont = dict(size = 25)
                     , marker_color=config.central))
# fig.add_trace(go.Bar(name="DesisSw", x=[" "], y=[30545075.4], legendrank=4, width=[0.18]
#                      , marker_line_color='rgb(255,161,90)', marker_pattern_shape="-"))
# fig.update_traces(marker_color='rgb(158,202,225)', marker_line_color='rgb(8,48,107)', marker_line_width=1.5, opacity=0.6)


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
        title_text="events(pre-results)/sec",
        titlefont=dict(size=30),
        exponentformat="e",
        ticktext=["0", "3M", "6M", "9M", "12M", "15M", "18M"],
        tickvals=[0, 3000000, 6000000, 9000000, 12000000, 15000000, 18000000],
        tickmode="array",
        range=[0, 18100000],
        tickfont=dict(size=35),
        # ticks="inside",
        # ticklen=20,
        # tickwidth =5,
    ),
    xaxis=dict(
        # title_text="local nodes",
        # titlefont=dict(size=15),
        ticktext=["Root", "Intermediate", "Local"],
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
# fig.update_yaxes(showgrid=True, gridwidth=1, gridcolor='rgb(120,120,120)')
# ,tickfont_family="Arial Black"

fig.show()

if not os.path.exists("E:\my paper\DesisPaper\Desis-Optimizing-Decentralized-Window-Aggregation\experiment\s1"):
    os.mkdir("E:\my paper\DesisPaper\Desis-Optimizing-Decentralized-Window-Aggregation\experiment\s1")
# fig.write_image("images/fig1.svg")
pio.write_image(fig, "E:\my paper\DesisPaper\Desis-Optimizing-Decentralized-Window-Aggregation\experiment\s1\/scalability\ThroughputMedianRoot.pdf")
pio.write_image(fig, "E:\my paper\DesisPaper\Desis-Optimizing-Decentralized-Window-Aggregation\experiment\s1\/scalability\ThroughputMedianRoot.svg")